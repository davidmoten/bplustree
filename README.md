# bplustree
<a href="https://travis-ci.org/davidmoten/bplustree"><img src="https://travis-ci.org/davidmoten/bplustree.svg"/></a><br/>
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/bplustree/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.davidmoten/bplustree)<br/>
[![codecov](https://codecov.io/gh/davidmoten/bplustree/branch/master/graph/badge.svg)](https://codecov.io/gh/davidmoten/bplustree)<br/>

**Status:** beta

Disk based B+-tree in java using memory mapped files (size limited only be available disk space).

## Features
* size only limited by available disk
* supports range queries
* optionally supports duplicate keys
* much faster read and write than H2 file-based database (because no transactions and different persistence model).

## Requirements

* fast read time for range queries by time and key
* fast insert time
* single node implementation (not distributed)
* use memory-mapped files for speed
* fixed size keys
* variable size values
* very large size storage (>2GB of keys or values)
* optimized for insert in approximate index order
* single threaded
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

Lets create a file based index of timestamped strings (for example lines from a log). Timestamps don't have to be unique.

```java
BPlusTree<Long, String> tree = 
  BPlusTree 
    .file()
    .directory(indexDirectory)
    .maxLeafKeys(32)
    .maxNonLeafKeys(8)
    .segmentSizeMB(1)
    .keySerializer(Serializer.LONG)
    .valueSerializer(Serializer.utf8())
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

## Using bplustree for String keys
Suppose you want to create a B-+ tree with String keys and those keys can have effectively arbitrary length. Keys are stored as fixed size records (unlike values which can be arbitrary in length). You can use hashes to get good find performance and keep the keys small (4 bytes of hash code) by making a tree of type:

```java
BPlusTree<Integer, StringWithValue> tree = ...
```
So you insert the String hashcode in the key and combine the String with the value. You find records using the hashcode of the String key and then filter the results based on an exact match of the String component of StringAndValue.

## Design
B+-tree index is stored across multiple files (of fixed size). Pointers to values are stored in the tree and the values are stored across a separate set of files (of fixed size).

A LargeByteBuffer abstracts access via Memory Mapped Files to a set of files (ByteBuffer only offers int positions which restricts size to 2GB, LargeByteBuffer offers long positions with no effective limit of size (apart from available disk)).
