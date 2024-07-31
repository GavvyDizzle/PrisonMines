# Events

This plugin has 3 custom events

## Competition Completion

* Called whenever a competition ends
* The list of Runs is ordered by their placement in the competition
* If a competition is ended via command and no rewards were given, then `ParkourCompetitionCompleteEvent#wereRewardsGiven()` will return false

## Player Competition Finish

* Called whenever a player completed the current competition course

## Player Personal Best

* Called whenever a player sets a new best time on a course
* If the time is the course record, then `ParkourPersonalBestEvent#isCourseRecord()` will return true
