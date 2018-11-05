
## Apache Orc [VPC Flow Logs](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/flow-logs.html)

Any code, applications, scripts, templates, proofs of concept,
documentation and other items are provided for illustration purposes only.

### Setup

Install Java 8 and [Gradle](https://docs.gradle.org/current/userguide/installation.html)

```
gradle run
```


### Results

Rows: 1mm - Raw text size: 104.7 MB - Apache Orc file is 3.2% smaller than just compressed text


| Format          | Compressed |  File        | Method          |
| --------------- | ---------- | ------------ | --------------- |
| Compressed Text | 6.2 MB     | raw.txt.gz   | zlib -9         |
| Apache Orc      | 6.0 MB     | columnar.orc | packed and zlib |

