package it.galeone_dev.santos.hibernate.abstractions;

import it.galeone_dev.santos.GetCollection;
import it.galeone_dev.santos.hibernate.models.AssignedJobOrder;
import it.galeone_dev.santos.hibernate.models.DummyMachineEvent;
import it.galeone_dev.santos.hibernate.models.Maintenance;
import it.galeone_dev.santos.hibernate.models.NonWorkingDay;
import it.galeone_dev.santos.hibernate.models.Sampling;
import it.galeone_dev.santos.hibernate.models.WorkingDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.hibernate.Session;

public abstract class DroppableMachineEvent implements MachineEvent {
    // returns moved events
    public static Collection<MachineEvent> shiftRight(MachineEvent e, Session hibSession) {
        // Global events after e and the same day of e
        Collection<NonWorkingDay> nonWorkingDaysAfterEvent = GetCollection.nonWorkingDaysAfterEvent(e);
        Collection<NonWorkingDay> nonWorkingDaysInConflictWith = GetCollection.nonWorkingDaysTheSameDayOf(e);
        
        Collection<Event> toSkip = new LinkedList<Event>(nonWorkingDaysAfterEvent);
        
        Collection<DroppableMachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        Queue<MachineEvent> movedEvents = new LinkedList<MachineEvent>();
        
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        
        // per ogni evento maccina in conflitto con il nuovo evento
        Queue<MachineEvent> qq = new LinkedList<MachineEvent>(machineEventsInConflict);
        
        // genero la lista degli eventi macchina da dover sistamare, perché a
        // causa
        // di un evento globale ho dovuto shiftare l'evento su un evento
        // maccihna già occupato
        // per ogni evento globale in conflitto con il nuovo evento
        if (nonWorkingDaysInConflictWith.size() != 0) {
            qq.add(e); // se sto spostando su un evento globale sicuramente
                       // dovrà muovermi, dato che lui sta fisso
        }
        
        // per ogni evento macchina in conflitto con il nuovo evento macchina
        while (!qq.isEmpty()) {
            MachineEvent conflictEvent = qq.poll();
            cal.setTime(conflictEvent.getStart());
            cal.add(Calendar.DATE, 1);
            Date nextDate = cal.getTime();
            // controlla se si può spostare nel giorno
            // successivo
            // cioè se il giorno successivo non cade in un evento globale
            boolean canMove = false;
            String nextDateString = sdf.format(nextDate);
            
            while (!canMove) {
                // collisione con evento globale (voglio spostarmi in un
                // posto già occupato da un evento globale? se sì collisione)
                boolean collision = false;
                for (Event skip : toSkip) {
                    if (sdf.format(skip.getStart()).equals(nextDateString)) {
                        collision = true;
                        break;
                    }
                }
                
                if (!collision) {
                    canMove = true;
                } else {
                    cal.add(Calendar.DATE, 1);
                    nextDate = cal.getTime();
                    nextDateString = sdf.format(nextDate);
                }
            }
            
            // nextDate ha dentro il prima giorno successivo senza eventi
            // globali
            
            // ora aggiorna l'evento in conflitto nella nuova data,
            // dopo controlla se esistono eventi in conflitto con l'evento in
            // conflitto
            // appena salvato e aggiungili alla lista di eventi da spostare
            
            long last = EventUtils.getLast(conflictEvent) * 60000;
            conflictEvent.setStart(nextDate);
            conflictEvent.setEnd(new Date(nextDate.getTime() + last));
            movedEvents.add(conflictEvent);
            conflictEvent = (MachineEvent) hibSession.merge(conflictEvent);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
            
            Collection<DroppableMachineEvent> newConflicts = GetCollection.machineEventsInConflictWith(conflictEvent);
            // remove events already present (avoid duplicate -> avoid loops)
            qq.removeAll(newConflicts);
            // add elements without duplicate
            qq.addAll(newConflicts);
        }
        
        Collection<MachineEvent> ret = new LinkedList<MachineEvent>(movedEvents);
        
        while(!movedEvents.isEmpty()) {
            MachineEvent moved = movedEvents.poll();
            Long movedLast = EventUtils.getLast(moved), dayLast = EventUtils.getLast(WorkingDay.get(moved.getStart()));
            if (movedLast > dayLast) {
                moved.setEnd(new Date(moved.getStart().getTime() + dayLast * 60000));
                hibSession.merge(moved);
                hibSession.getTransaction().commit();
                hibSession.getTransaction().begin();
                
                Long remainingTime = movedLast - dayLast;
                while (remainingTime > 0) {
                    moved.setStart(EventUtils.tomorrow(moved.getStart()));
                    dayLast = EventUtils.getLast(WorkingDay.get(moved.getStart()));
                    Long eventLast = remainingTime > dayLast ? dayLast : remainingTime;
                    moved.setEnd(new Date(moved.getStart().getTime() + eventLast * 60000));
                    hibSession.save(moved);
                    // fuck you hibernate (again)
                    hibSession.getTransaction().commit();
                    hibSession.getTransaction().begin();
                    // remove already Moved events
                    Collection<MachineEvent> innerMoved = shiftRight(moved, hibSession);
                    movedEvents.removeAll(innerMoved);
                    //callMerge(innerMoved, hibSession);
                    remainingTime -= eventLast;
                }
            }
        }
        return ret;
    }
    
    private static void callMerge(Collection<MachineEvent> moved, Session hibSession) {
        for(MachineEvent m : moved) {
            if(m.getClass().equals(AssignedJobOrder.class)) {
                AssignedJobOrder.merge((AssignedJobOrder)m, hibSession);
            } else if(m.getClass().equals(Maintenance.class)) {
                Maintenance.merge((Maintenance)m, hibSession);
            } if(m.getClass().equals(Sampling.class)) {
                Sampling.merge((Sampling)m, hibSession);
            }
        }
    }
    
    private Date oldStart;
    
    public Date getOldStart() {
        return oldStart;
    }
    
    public void setOldStart(Date oldStart) {
        this.oldStart = oldStart;
    }
    
    public static void switchOn(DroppableMachineEvent e, Session hibSession, StringBuilder message) {
        Collection<DroppableMachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        
        if (GetCollection.nonWorkingDaysTheSameDayOf(e).size() > 0) {
            message.replace(0, message.length(), "Non puoi produrre in un giorno non lavorativo");
            return;
        }
        
        Long myLast = EventUtils.getLast(e), maxLast = EventUtils.getLast(WorkingDay.get(e.getStart()));
        
        if (myLast > maxLast) {
            message.replace(0, message.length(), "Non puoi spostare un evento di " + (myLast / 60)
                    + " ore su una giornata lavorativa di " + (maxLast / 60) + " ore.");
            return;
        }
        
        // Per evitare che lo switch all'indietro dell'evento che è persente sulla destinazione
        // vada a sforare il numero di ore della giornata
        // prento il numero di ore della giornata da cui questo evento parte (oldstart)
        // e controllo se numeroDiOreIniziali + ore dell'evento in conflitto ci sta
        // se ci sta, ok, altrimenti interrompi
        DummyMachineEvent originalPosition = new DummyMachineEvent();
        originalPosition.setMachine(e.getMachine());
        originalPosition.setStart(e.getOldStart());
        originalPosition.setEnd(new Date(e.getOldStart().getTime() + myLast * 60000));
        Collection<DroppableMachineEvent> eventsInStartingDate = GetCollection.machineEventsTheSameDayOf(originalPosition);
        // rimuovo me stesso dagli eventi che vengono spostati
        // o almeno dal conteggio
        eventsInStartingDate.remove(e);
        
        Long startinHours = 0L;
        for (DroppableMachineEvent ev: eventsInStartingDate) {
            startinHours += EventUtils.getLast(ev);
        }
        
        Long movinHours = 0L;
        for (DroppableMachineEvent conflictEvent : machineEventsInConflict) {
            movinHours += EventUtils.getLast(conflictEvent);
        }
        
        if(startinHours + movinHours > maxLast) {
            message.replace(0, message.length(), "L'operazione di switch causerebbe un numero di ore sulla data di inizio (da cui hai iniziato il trascinamento)"
                    + "superiori alle ore della giornata lavorativa.");
            return;
        }
        
        for (DroppableMachineEvent conflictEvent : machineEventsInConflict) {
            Date oldStart = conflictEvent.getStart();
            conflictEvent.setEnd(new Date(e.getOldStart().getTime() + EventUtils.getLast(conflictEvent) * 60000));
            conflictEvent.setStart(e.getOldStart());
            conflictEvent = (DroppableMachineEvent) hibSession.merge(conflictEvent);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
            conflictEvent.setOldStart(oldStart);
        }
    }
    
}
