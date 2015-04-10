function dataTablesCheckbox( data, type, full, meta ) {
    return data === true || data === 'true' ? "Si" : "No";
};

function dataTablesColor( data, type, full, meta ) {
	return '<div style="background-color:'+data+';color:#000">'+data+'</div>';
};

function dataTablesLeadTime( data, type, full, meta ) {
	var leadTime    = parseInt(data),
		hours       = leadTime % 24,
		days        = Math.floor(leadTime / 24),
		daysString  = days === 0  ? "" : days  === 1 ? "1 giorno" : days  + " giorni",
		hoursString = hours === 0 ? "" : hours === 1 ? "1 ora"    : hours + " ore",
		ret = isNaN(leadTime) ? data : daysString + (daysString !== "" && hoursString !== "" ? " e " : "") + hoursString;
	return ret === "" ? "Ore completamente assegnate" : ret;
};


$.editable.addInputType('leadtime', {
	  element: function(settings, original) {
	    $(this).append('<label>Giorni <input type="number" name="giorni" class="giorni" /></label>' +
	    		'<label>Ore<input type="number" name="ore" class="ore" /></label>');
	    var hidden = $('<input type="hidden" value="0"/>');
	    $(this).append(hidden);
	    return(hidden);
	  },

	  submit: function(settings, original) {
		var days = parseInt($(".giorni", this).val()),
			hours =  parseInt($(".ore", this).val());
		days  = isNaN(days)  ? 0 : days;
		hours = isNaN(hours) ? 0 : hours;
		
		$(':hidden', this).val(days * 24 + hours);
	  }
});
