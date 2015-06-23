    function buildCal(m, y, cM, cH, cDW, cD, brdr, events){
	var mn = ['January','February','March','April','May','June','July','August','September','October','November','December'];
	var dim = [31,0,31,30,31,30,31,31,30,31,30,31];

    // calculate the offset from Sunday at the beginning of the month
	var oD = new Date(y, m-1, 1); //DD replaced line to fix date bug when current day is 31st
	oD.od = oD.getDay()+1; //DD replaced line to fix date bug when current day is 31st
	
	var todaydate = new Date(); //DD added
	var scanfortoday = (y == todaydate.getFullYear() && m == (todaydate.getMonth() + 1))? todaydate.getDate() : 0; //DD added
	
	// check for leap year, set February number of days (28 or 29)
	dim[1] = (((oD.getFullYear() % 100 !== 0) && (oD.getFullYear() % 4 === 0)) || (oD.getFullYear() % 400 === 0)) ? 29 : 28;
	
	var t = '<div class="' + cM +'" >';
	t += '<div class="' + cH + '">' + mn[m - 1] + ' - ' + y + '</div>'; // month and year header
	
	for(w = 0; w < 5; w++) { //creates 35 repeating day names, 21 / 3 = 7, 7 x 5 = 35 total
		for(s = 0; s < 21; s += 3) t += '<div class="' + cDW + '">' + "SunMonTueWedThuFriSat".substr(s,3) + '</div>';
	}
	t += '<div class="' + cDW + '">Sun</div><div class="' + cDW + '">Mon</div>'; //add two more day names to make 37 total
	
	t += '<div style="clear:both"></div>'; //clear the float:left
	
	//collect events for this month
	var currentEvents = [], start, end, span;
	for(e = 0; e < events.length; e++) {
		start = new Date(events[e].start);
		end = new Date(events[e].end);
		if((start.getMonth() + 1) == m && start.getFullYear() == y) {
			span = Math.round((end.valueOf() - start.valueOf()) / (24 * 60 * 60 * 1000));
			// add additional information to the array
			events[e].span = span;  //span of the event in days
			events[e].day = start.getDate(); //date of the event
			events[e].offset = oD.od - 1; //month start offset
			events[e].startDate = start; //start date object
			events[e].endDate = end; //end date object
			events[e].used = false;  //needed later for sorting
			events[e].top = 0; //needed later for positioning
			currentEvents.push(events[e]);
		}
	}
	
	var sortedDates = [];
	for (var key in currentEvents) {
		sortedDates.push(currentEvents[key].startDate); //copy the startDates into a simple array
	}
	sortedDates.sort(date_sort_asc); //sort the start dates
	var sortedEvents = currentEvents;
	for (se = 0; se < sortedDates.length; se++) {
		for (ce = 0; ce < sortedDates.length; ce++) { //match up the unsorted with the sorted startDates.
			if(currentEvents[ce].startDate === sortedDates[se] && (!currentEvents[ce].used)) {
				sortedEvents[ce].sortKey = se; // add a sort key to the sorted array of events
				currentEvents[ce].used = true; //2 events may have exactly the same time stamp, "true" will prevent the same event from being added twice
			} 
		}
	}
	
	//look for overlapping events
	var monthDays = daysInMonth(m, y);//number of days in the current month
	var overlaps = [];
	for (de = 0; de < sortedEvents.length; de++) {
		var id = sortedEvents[de].id;
		var sDate = sortedEvents[de].startDate.getDate();
		var sYear = sortedEvents[de].startDate.getFullYear();
		var sd = sDate + '-' + sYear; //combine date-year
		var sd1, span1 = sortedEvents[de].span;
		for (re = 0; re < span1; re++) { //events can span days. Event days other than the start day can overlap another event's days
			for (ee = 0; ee < sortedEvents.length; ee++) {
				sd1 = sortedEvents[ee].startDate.getDate() + '-' + sortedEvents[de].startDate.getFullYear();//combine date-year
				if (sd === sd1 && (sortedEvents[ee].id !== id)) overlaps.push(id + ',' + sortedEvents[ee].id);//compare
			}
			sDate = ((sDate + 1) > monthDays) ? 1 : sDate + 1;//advance date for multi-day events
			sd = sDate + '-' + sYear;
		}
	}
	
	var unique = overlaps.filter(onlyUnique); //save only unique overlaps
	var unique1 = [];
	//unique array may still include duplicate reverse order overlaps like "2,7" and "7,2"
	//this loop eliminates reverse order duplicates
	for (ge = 0; ge < unique.length; ge++) {
		if (ge === 0) unique1.push(unique[ge]); //add the first item to unique1
		for (he = 0; he < unique1.length; he++) {
			var a = unique[ge].split(',');//split so we can reverse the order and compare
			if ((a[1] + ',' + a[0]) === unique1[he] ||unique[ge] === unique1[he]) {
				break; //break if both the forward or reverse of the pair exists in unique1...
			} else { //...otherwise add unique[ge] to unique1
				unique1.push(unique[ge]);
				break; 
			} 
		}
	}
	
	//only unique pairs of overlaps remain in unique1 array
	//now adjust the "top" property so later we can use it to stack events one above the other on calendar days
	for (te = 0; te < unique1.length; te++) {
		var ids = unique1[te].split(','), sortkeyA = "", sortkeyB = "";
		for (be = 0; be < sortedEvents.length; be++) {
			if (sortedEvents[be].id === ids[0]) sortkeyA = ids[0] + ',' + sortedEvents[be].sortKey;
			if (sortedEvents[be].id === ids[1]) sortkeyB = ids[1] + ',' + sortedEvents[be].sortKey;
		}
		var sortA = sortkeyA.split(',');
		var sortB = sortkeyB.split(',');
		var first = (sortA[1] > sortB[1]) ? sortA[0] : sortB[0];
		for (we = 0; we < sortedEvents.length; we++) {
			if (sortedEvents[we].id === first) {
				sortedEvents[we].top = sortedEvents[we].top + 1;
			}
		}
	}
	
	var daysPrevMonth = ((m - 1) === 0) ? dim[dim.length-1] : dim[m-2];//previous month number of days
	for(i = 1; i <= 37; i++){ // create 37 boxes for days of the month
		var x = '';
		if ((i - oD.od) < 0) { //print out previous month's days			
			x = '<span style="color:#ccc">' + (daysPrevMonth + (i - (oD.od-1))) + '</span>';
		} else if (((i - oD.od) >= 0) && ((i - oD.od) < dim[m-1])) { //print out this month's days
			x = (i - oD.od) + 1;
		} else { //print out next month's days
			x = '<span style="color:#ccc">' + (i - (dim[m-1] + (oD.od-1))) + '</span>';
		}
		
		/*if (x == scanfortoday) {// find today
			x = '<span id="today">' + x + '</span>'; // highlight today
		}*/
		 
		for(me = 0; me < sortedEvents.length; me++) {
			if ((sortedEvents[me].day + sortedEvents[me].offset) == i) { // add in the current events
				x += '<div class="eventBar" style="margin-top:' + (sortedEvents[me].top * 20) + 'px;width:46px; background-color:'+sortedEvents[me].color+'">' + sortedEvents[me].title + '</div>';
			}
		}
		t += '<div class="' + cD + '">' + x + '</div>';
	}
	return t += '</div>';
}

//date sorting function
var date_sort_asc = function (date1, date2) {
  if (date1 > date2) return 1;
  if (date1 < date2) return -1;
  return 0;
};

//returns days in a given month and year
function daysInMonth(month,year) {
    return new Date(year, month, 0).getDate();
}

//eliminates duplicates from an array
function onlyUnique(value, index, self) { 
    return self.indexOf(value) === index;
}

function changeMonth(direction) {
	if (direction) {
		selectedyear =  ((selectedmonth+1) > 12) ? selectedyear+1 : selectedyear;
		selectedmonth = ((selectedmonth+1) > 12) ? 1 : selectedmonth+1;
	} else {
		selectedyear =  ((selectedmonth-1) == 0) ? selectedyear-1 : selectedyear;
		selectedmonth = ((selectedmonth-1) == 0) ? 12 : selectedmonth-1;
	}
	document.getElementById('cal').innerHTML = buildCal(selectedmonth ,selectedyear, "main", "month", "daysofweek", "days", 1);
}