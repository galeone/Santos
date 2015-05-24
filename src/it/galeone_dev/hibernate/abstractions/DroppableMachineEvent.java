package it.galeone_dev.hibernate.abstractions;

import it.galeone_dev.GetCollection;
import it.galeone_dev.hibernate.models.NonWorkingDay;
import it.galeone_dev.hibernate.models.WorkingDay;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.hibernate.Session;

public abstract class DroppableMachineEvent implements MachineEvent {
    
    public static void shiftRight(MachineEvent e, Session hibSession) {
        // Global events after e and the same day of e
        Collection<NonWorkingDay> nonWorkingDaysAfterEvent = GetCollection.nonWorkingDaysAfterEvent(e);
        Collection<NonWorkingDay> nonWorkingDaysInConflictWith = GetCollection.nonWorkingDaysTheSameDayOf(e);
        
        Collection<Event> toSkip = new LinkedList<Event>(nonWorkingDaysAfterEvent);
        
        Collection<DroppableMachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        

        // per ogni evento maccina in conflitto con il nuovo evento
        Queue<MachineEvent> qq = new LinkedList<MachineEvent>(machineEventsInConflict);
        
        // genero la lista degli eventi macchina da dover sistamare, perché a causa
        // di un evento globale ho dovuto shiftare l'evento su un evento maccihna già occupato
        // per ogni evento globale in conflitto con il nuovo evento
        if(nonWorkingDaysInConflictWith.size() != 0) {
            qq.add(e); // se sto spostando su un evento globale sicuramente dovrà muovermi, dato che lui sta fisso
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
            
            // nextDate ha dentro il prima giorno successivo senza eventi globali
            
            // ora aggiorna l'evento in conflitto nella nuova data,
            // dopo controlla se esistono eventi in conflitto con l'evento in conflitto
            // appena salvato e aggiungili alla lista di eventi da spostare

            long last = EventUtils.getLast(conflictEvent) * 60000;
            conflictEvent.setStart(nextDate);
            conflictEvent.setEnd(new Date(nextDate.getTime() + last));
            // hibSession.saveOrUpdate(conflictEvent);
            conflictEvent = (MachineEvent) hibSession.merge(conflictEvent);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
                        
            Collection<DroppableMachineEvent> newConflicts = GetCollection.machineEventsInConflictWith(conflictEvent);
            // remove events already present (avoid duplicate -> avoid loops)
            qq.removeAll(newConflicts);
            // add elements without duplicate
            qq.addAll(newConflicts);
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
        
        if(GetCollection.nonWorkingDaysTheSameDayOf(e).size() > 0) {
            message.replace(0,message.length(),"Non puoi produrre in un giorno non lavorativo");
            return;
        }
        
        Long myLast = EventUtils.getLast(e), maxLast = EventUtils.getLast(WorkingDay.get(e.getStart()));

        if(myLast > maxLast ) {
            message.replace(0,message.length(),"Non puoi spostare un evento di " + (myLast / 60) + " ore su una giornata lavorativa di "  + (maxLast / 60) + " ore.");
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
            switchOn(conflictEvent, hibSession, message);
        }
    }
    
}
