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

public abstract class DroppableGlobalEvent implements GlobalEvent {
    // shift EVERYTHING RIGHT
    public static void shiftMachineEventsRight(GlobalEvent e, Session hibSession) {
        // Global events after e (to jump when rearranging items)
        Collection<GlobalEvent> globalEventsAfterGlobalEvent = GetCollection.globalEventsAfter(e);
        // Ottiene la lista di tutti gli eventi (di tutte le macchine) in conflitto con la data di e
        // e la lista di tutti gli eventi (di tutte le macchine) successivi alla data di e
        Collection<MachineEvent> machineEventsInConflict = GetCollection.machineEventsTheSameDayOf(e);
        // machine events must be shifted if required
        Collection<MachineEvent> machineEventsAfter = GetCollection.machineEventsAfter(e);
        // globalEventsAfter does not
        
        // per ogni evento (macchina o globale) macchina in conflitto con e
        Calendar cal = Calendar.getInstance(EventUtils.timezone);
        Queue<MachineEvent> qq = new LinkedList<MachineEvent>(machineEventsInConflict);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        while (!qq.isEmpty()) {
            MachineEvent conflictEvent = qq.poll();
            cal.setTime(conflictEvent.getStart());
            cal.add(Calendar.DATE, 1);
            Date nextDate = cal.getTime();
            // controlla se si può spostare nel giorno successivo
            // cioè se il giorno successivo non cade in un evento globale
            boolean canMove = false;
            String nextDateString = sdf.format(nextDate);
            
            while (!canMove) {
                boolean collision = false;
                for (GlobalEvent ea : globalEventsAfterGlobalEvent) {
                    //if dragged on the past (move back), I should remove myself (e: skip, no collision) from the collection
                    if (!ea.equals(e) && sdf.format(ea.getStart()).equals(nextDateString)) {
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
            
            for (MachineEvent me : machineEventsAfter) {
                if (sdf.format(me.getStart()).equals(nextDateString) // stessa data
                        // se non potevo mettercelo, aggiungo quello che
                        // va in conflitto alla lista di quelli da
                        // sistemare
                   ) { qq.add(me); }
            }
            
            // metto in nextdate l'ajconflict
            long last = conflictEvent.getEnd().getTime() - conflictEvent.getStart().getTime();
            conflictEvent.setStart(nextDate);
            conflictEvent.setEnd(new Date(nextDate.getTime() + last));
            //hibSession.saveOrUpdate(conflictEvent);
            hibSession.merge(conflictEvent);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
        }
    }
}
