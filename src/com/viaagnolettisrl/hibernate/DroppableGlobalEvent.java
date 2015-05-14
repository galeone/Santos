package com.viaagnolettisrl.hibernate;


import java.util.Collection;
import java.util.Date;

import org.hibernate.Session;

import com.viaagnolettisrl.EventUtils;
import com.viaagnolettisrl.GetCollection;

public abstract class DroppableGlobalEvent implements GlobalEvent {
    // shift every machine events in conflict with e right
    public static void shiftMachineEventsRight(GlobalEvent e, Session hibSession) {
        Collection<MachineEvent> machineEventsInConflict = GetCollection.machineEventsTheSameDayOf(e);
        for(MachineEvent me : machineEventsInConflict) {
            Long last = EventUtils.getLast(me);
            me.setStart(EventUtils.tomorrow(me.getStart()));
            me.setEnd(new Date(me.getStart().getTime() + last * 60000));
            // tomorrow
            hibSession.merge(me);
            hibSession.getTransaction().commit();
            hibSession.getTransaction().begin();
            DroppableMachineEvent.shiftRight(me, hibSession);
        }

    }
}

