
## Apache ORC [VPC Flow Logs](http://docs.aws.amazon.com/AmazonVPC/latest/UserGuide/flow-logs.html)

Any code, applications, scripts, templates, proofs of concept,
documentation and other items are provided for illustration purposes only.

Copyright 2017 Amazon Web Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


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

