# logss
Disk based B+-tree in java designed for querying of metrics (key-value pairs) from logs (time-based). 

## Requirements

* fast read time for range queries by time and key
* fast insert time
* no updates
* single node implementation (not distributed)
* support truncate (throw away old stuff) and maintain perf requirements
