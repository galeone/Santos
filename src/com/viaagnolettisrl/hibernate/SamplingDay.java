package com.viaagnolettisrl.hibernate;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;

import org.hibernate.Session;

import com.viaagnolettisrl.GetCollection;

public class SamplingDay implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date start, end;
	
	public String title = "Campionamento",
			color = "#00E",
			type = "samplingday";
	public boolean overlap = false, // can't drop events on a non working day
			allDay = true,
			editable = true;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SamplingDay other = (SamplingDay) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

    @Override
    public String toString() {
        return "SamplingDay [id=" + id + ", start=" + start + ", title=" + title + "]";
    }
    
    public static void handle(SamplingDay sd, Session hibSession) {
        
        // Cerca assigned job order nel giorno di
        // campionamento e fai slittare ai giorni lavorativi
        // successivi
        // lavorativi = non di campionamento e non non
        // lavorativi
        Collection<AssignedJobOrder> assignedJobOrderConflict = GetCollection
                .assignedJobOrdersInConflictWith(sd);
        // ottieni la lista dei giorni di non lavoro
        // successivi al sampling
        Collection<NonWorkingDay> nonworkingDaysAfterSampling = GetCollection
                .nonWorkingDaysAfterSampling(sd);
        // ottieni la lista degli ajo successivi al sampling
        Collection<AssignedJobOrder> assignedJobOrdersAfterSampling = GetCollection
                .assignedJobOrdersAfterSampling(sd);
        // ottieni la lista dei giori di samplig successivi
        // a questo sampling
        Collection<SamplingDay> sampligDaysAfterSampling = GetCollection
                .samplingDaysAfterSampling(sd);
        
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
                for (NonWorkingDay nw : nonworkingDaysAfterSampling) {
                    if (sdf.format(nw.getStart()).equals(nextDateString)) {
                        collision = true;
                        break;
                    }
                }
                
                if (!collision) {
                    for (SamplingDay s : sampligDaysAfterSampling) {
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
            AssignedJobOrder toMove = null;
            for (AssignedJobOrder aj : assignedJobOrdersAfterSampling) {
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
        
        // se funziona sono Dio.
        
    }

}
