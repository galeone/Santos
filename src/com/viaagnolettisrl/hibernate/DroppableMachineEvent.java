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
        // Global events after e
        Collection<GlobalEvent> globalEventsAfterEvent = GetCollection.globalEventsAfter(e);
        // Cerca assigned job order nel giorno di
        // campionamento e fai slittare ai giorni lavorativi
        // successivi
        // lavorativi = non di campionamento e non non lavorativi
        // machine event
        Collection<MachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        Collection<MachineEvent> machineEventsAfter = GetCollection.machineEventsAfter(e);
        
        // per ogni evento macchina in conflitto con il nuovo evento
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        Queue<MachineEvent> qq = new LinkedList<MachineEvent>(machineEventsInConflict);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        
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
                boolean collision = false;
                for (GlobalEvent nw : globalEventsAfterEvent) {
                    if (sdf.format(nw.getStart()).equals(nextDateString)) {
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
            
            // se il giorno successivo è vuoto, piazzacelo e via
            // se non è vuoto, cioè c'è un evento della macchina
            // (per la tal macchina), piazza al giorno al
            // posto di questo
            // e reitera il procedimento usando il giorno
            // che hai appena spostato (in modo da spostarlo
            // in avanti), cioè aggiungi il giorno appena
            // tirato furi
            // alla lista dei giorni in conflitto
            
            MachineEvent toMove = null;
            for (MachineEvent me : machineEventsAfter) {
                if (sdf.format(me.getStart()).equals(nextDateString) // stessa data
                        //&& me.getMachine().getId().equals(conflictEvent.getMachine().getId())
                        // il fatto che sia sulla stessa macchina dovrebbe gestirlo il fetch degli
                        // eventi fatto un su MachineEvent che contiene la macchina
                   ) { toMove = me; break; }
            }
            
            // metto in nextdate l'ajconflict
            long last = conflictEvent.getEnd().getTime() - conflictEvent.getStart().getTime();
            conflictEvent.setStart(nextDate);
            conflictEvent.setEnd(new Date(nextDate.getTime() + last));
            //hibSession.saveOrUpdate(conflictEvent);
            hibSession.merge(conflictEvent);
            
            // se non potevo mettercelo, aggiungo quello che
            // va in conflitto alla lista di quelli da
            // sistemare
            if (toMove != null) {
                qq.add(toMove);
            }
        }
        
        // se funziona sono Dio.+
    }
    
    private Date oldStart;
    
    public Date getOldStart() {
        return oldStart;
    }

    public void setOldStart(Date oldStart) {
        this.oldStart = oldStart;
    }

    public static void switchOnNext(DroppableMachineEvent e, Session hibSession) {
        Collection<MachineEvent> machineEventsInConflict = GetCollection.machineEventsInConflictWith(e);
        for(MachineEvent conflictEvent : machineEventsInConflict) {
            conflictEvent.setEnd(new Date(e.getOldStart().getTime() + EventUtils.getLast(conflictEvent)*60000));
            conflictEvent.setStart(e.getOldStart());
            hibSession.merge(conflictEvent);
        }
    }
    
}
