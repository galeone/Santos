package com.viaagnolettisrl.hibernate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.hibernate.Session;

import com.viaagnolettisrl.EventUtils;
import com.viaagnolettisrl.GetCollection;

public abstract class DroppableMachineEvent implements MachineEvent {
    
    public static void shiftRight(MachineEvent e, Session hibSession) {
        // Global events after e and the same day of e
        Collection<GlobalEvent> globalEventsAfterEvent = GetCollection.globalEventsAfter(e);
        Collection<GlobalEvent> globalEventsInConflictWith = GetCollection.globalEventsTheSameDayOf(e);
        
        Collection<Event> toSkip = new LinkedList<Event>(globalEventsAfterEvent);
        
        Collection<DroppableMachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        

        // per ogni evento maccina in conflitto con il nuovo evento
        Queue<MachineEvent> qq = new LinkedList<MachineEvent>(machineEventsInConflict);
        
        // genero la lista degli eventi macchina da dover sistamare, perché a causa
        // di un evento globale ho dovuto shiftare l'evento su un evento maccihna già occupato
        // per ogni evento globale in conflitto con il nuovo evento
        if(globalEventsInConflictWith.size() != 0) {
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
            hibSession.merge(conflictEvent);
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
    
    public static void switchOnNext(DroppableMachineEvent e, Session hibSession, StringBuilder message) {
        Collection<DroppableMachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        Collection<GlobalEvent> globaEventsInConflictWith = GetCollection.globalEventsTheSameDayOf(e);
        if(globaEventsInConflictWith.size() > 0) {
            message.replace(0,message.length(),"Non puoi spostare un evento su un evento globale");
            return;
        }
        for (MachineEvent conflictEvent : machineEventsInConflict) {
            conflictEvent.setEnd(new Date(e.getOldStart().getTime() + EventUtils.getLast(conflictEvent) * 60000));
            conflictEvent.setStart(e.getOldStart());
            hibSession.merge(conflictEvent);
        }
    }
    
}
