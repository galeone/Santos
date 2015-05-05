package com.viaagnolettisrl.hibernate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.hibernate.Session;

import com.viaagnolettisrl.GetCollection;

public abstract class DraggableMachineEvent implements MachineEvent {
    
    public static void shiftRight(MachineEvent e, Session hibSession) {
        
        // Cerca assigned job order nel giorno di
        // campionamento e fai slittare ai giorni lavorativi
        // successivi
        // lavorativi = non di campionamento e non non lavorativi
        Collection<AssignedJobOrder> assignedJobOrderConflict = GetCollection.assignedJobOrdersInConflictWith(e);
        // ottieni la lista dei giorni di non lavoro
        // successivi al sampling
        Collection<NonWorkingDay> nonworkingDaysAfterEvent = GetCollection.nonWorkingDaysAfterEvent(e);
        // ottieni la lista degli ajo successivi al sampling
        Collection<AssignedJobOrder> assignedJobOrdersAfterEvent = GetCollection.assignedJobOrdersAfterEvent(e);
        // ottieni la lista dei giorni di samplig successivi
        // a questo sampling
        Collection<Sampling> sampligDaysAfterEvent = GetCollection.samplingAfterEvent(e);
        
        // per ogni ajo in confitto con il sampling
        Calendar cal = Calendar.getInstance();
        Queue<AssignedJobOrder> qq = new LinkedList<AssignedJobOrder>(assignedJobOrderConflict);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        
        while (!qq.isEmpty()) {
            AssignedJobOrder ajConflict = qq.poll();
            cal.setTime(ajConflict.getStart());
            cal.add(Calendar.DATE, 1);
            Date nextDate = cal.getTime();
            // controlla se si può spostare nel giorno
            // successivo
            // cioè se il giorno successivo non cade in un
            // giorno di chiusura o sampling
            boolean canMove = false;
            String nextDateString = sdf.format(nextDate);
            
            while (!canMove) {
                
                boolean collision = false;
                for (NonWorkingDay nw : nonworkingDaysAfterEvent) {
                    if (sdf.format(nw.getStart()).equals(nextDateString)) {
                        collision = true;
                        break;
                    }
                }
                
                if (!collision) {
                    for (Sampling s : sampligDaysAfterEvent) {
                        if (sdf.format(s.getStart()).equals(nextDateString)) {
                            collision = true;
                            break;
                        }
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
            
            // nextDate ha dentro il prima giorno successivo
            // senza sampling o chisure
            
            // se il giorno successivo è vuoto, piazzacelo e
            // via
            // se non è vuoto, cioè c'è della produzione
            // (per la tal macchina), piazza al giorno al
            // posto di questo
            // e reitera il procedimento usando il giorno
            // che hai appena spostato (in modo da spostarlo
            // in avanti), cioè aggiungi il giorno appena
            // tirato furi
            // alla lista dei giorni in conflitto
            
            // TODO: controllare solo per e.getMachine()
            AssignedJobOrder toMove = null;
            for (AssignedJobOrder aj : assignedJobOrdersAfterEvent) {
                if (sdf.format(aj.getStart()).equals(nextDateString)
                        && aj.getMachine().getId().equals(ajConflict.getMachine().getId())) {
                    toMove = aj;
                    break;
                }
            }
            
            // metto in nextdate l'ajconflict
            long last = ajConflict.getEnd().getTime() - ajConflict.getStart().getTime();
            ajConflict.setStart(nextDate);
            ajConflict.setEnd(new Date(nextDate.getTime() + last));
            hibSession.saveOrUpdate(ajConflict);
            
            // se non potevo mettercelo, aggiungo quello che
            // va in conflitto alla lista di quelli da
            // sistemare
            if (toMove != null) {
                qq.add(toMove);
            }
        }
        
        // se funziona sono Dio.+
    }
    
}
