# bplustree
Disk based B+-tree in java designed for querying of metrics (key-value pairs) from logs (time-based). 

## Requirements

* fast read time for range queries by time and key
* fast insert time
* no updates
* single node implementation (not distributed)
* support truncate (throw away old stuff) and maintain perf requirements
* use memory-mapped files for speed
* fixed size keys
* variable size values
* very large size storage (>2GB of keys or values)
* optimized for insert in approximate index order
* single threaded (no concurrency support)
* no transactions
* delete not supported (?)

## Getting started
Add this to your pom.xml:

```xml
<dependency>
  <groupId>com.github.davidmoten</groupId>
  <artifactId>bplustree</artifactId>
  <version>VERSION_HERE</version>
</dependency>
```

## Example

Lets create a file based index of timestamped strings. Timestamps don't have to be unique.

```java
BPlusTree<Long, String> tree = 
  BPlusTree 
    .file()
    .directory(indexDirectory)
    .maxLeafKeys(32)
    .maxNonLeafKeys(8)
    .segmentSizeMB(1)
    .keySerializer(Serializer.LONG)
    .valueSerializer(Serializer.utf8(0))
    .naturalOrder();
    
// insert some values    
tree.insert(1000L, "hello");
tree.insert(2000L, "there");

// search the tree for values with keys between 0 and 3000
// and print out key value pairs
tree.findEntries(0, 3000).forEach(System.out.println);

// search the tree for values with keys between 0 and 3000
// and print out values only
tree.find(0, 3000).forEach(System.out.println);
```
## Duplicate keys
Duplicate keys are allowed by default. You can force overwrite of keyed values by setting `.unique(false)` in the builder.

Note that for efficiency values with duplicate keys are entered into the tree in reverse insert order so to extract the values retaining insert order a special method is used:

```java
tree.findOrderPreserving(0, 3000);
```

## Design
B+-tree index is stored across multiple files (of fixed size). Pointers to values are stored in the tree and the values are stored across a separate set of files (of fixed size).

A LargeByteBuffer abstracts access via Memory Mapped Files to a set of files (ByteBuffer only offers int positions which restricts size to 2GB, LargeByteBuffer offers long positions with no effective limit of size (apart from available disk).
