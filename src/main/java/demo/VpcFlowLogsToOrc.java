/**
 * Any code, applications, scripts, templates, proofs of concept,
 * documentation and other items are provided for illustration purposes only.
 *
 * Copyright 2017 Amazon Web Services
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Bastion stack creation prerequisite:  first create an EC2 key pair and a VPC stack.
 * For details about how to connect to a Linux instance in a private subnet via the
 * bastion, see the following AWS blog post:
 * https://aws.amazon.com/blogs/security/securely-connect-to-linux-instances-running-in-a-private-amazon-vpc/
 */

package demo;

// Orc
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.exec.vector.BytesColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.LongColumnVector;
import org.apache.hadoop.hive.ql.exec.vector.VectorizedRowBatch;
import org.apache.orc.OrcFile;
import org.apache.orc.TypeDescription;
import org.apache.orc.Writer;

// Java
import java.util.zip.GZIPInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.InetAddress;

public class VpcFlowLogsToOrc {


  public static void main(final String [] pArgs) throws Exception {


    Files.deleteIfExists(Paths.get("columnar.orc"));

    final Configuration conf = new Configuration();

    final TypeDescription schema = TypeDescription.createStruct()
    .addField("version", TypeDescription.createString())
    .addField("account_idd", TypeDescription.createString())
    .addField("interface_id", TypeDescription.createString())
    .addField("src_addr", TypeDescription.createString())
    .addField("dst_addr", TypeDescription.createString())
    .addField("src_port", TypeDescription.createInt())
    .addField("dst_port", TypeDescription.createInt())
    .addField("protocol", TypeDescription.createShort())
    .addField("packets", TypeDescription.createInt())
    .addField("bytes", TypeDescription.createLong())
    .addField("start_epoch", TypeDescription.createLong())
    .addField("end_epoch", TypeDescription.createLong())
    .addField("action", TypeDescription.createString())
    .addField("log_status", TypeDescription.createString());

    final Writer writer = OrcFile.createWriter(new Path("columnar.orc"), OrcFile.writerOptions(conf).setSchema(schema));

    final VectorizedRowBatch batch = schema.createRowBatch();

    final BytesColumnVector versionVector = (BytesColumnVector) batch.cols[0];
    final BytesColumnVector accountIdVector = (BytesColumnVector) batch.cols[1];
    final BytesColumnVector interfaceIdVector = (BytesColumnVector) batch.cols[2];
    final BytesColumnVector srcAddrVector = (BytesColumnVector) batch.cols[3];
    final BytesColumnVector dstAddrVector = (BytesColumnVector) batch.cols[4];
    final LongColumnVector srcPortVector = (LongColumnVector) batch.cols[5];
    final LongColumnVector dstPortVector = (LongColumnVector) batch.cols[6];
    final LongColumnVector protocolVector = (LongColumnVector) batch.cols[7];
    final LongColumnVector packetsVector = (LongColumnVector) batch.cols[8];
    final LongColumnVector bytesVector = (LongColumnVector) batch.cols[9];
    final LongColumnVector startVector = (LongColumnVector) batch.cols[10];
    final LongColumnVector endVector = (LongColumnVector) batch.cols[11];
    final BytesColumnVector actionVector = (BytesColumnVector) batch.cols[12];
    final BytesColumnVector logStatusVector = (BytesColumnVector) batch.cols[13];

    final BufferedReader in
    = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream("raw.txt.gz"))));

    String line;

    while ((line = in.readLine()) != null) {
      int row = batch.size++;

      final String [] fields = line.split(" ");

      versionVector.setVal(row, fields[0].getBytes("UTF8"));
      accountIdVector.setVal(row, fields[1].getBytes("UTF8"));
      interfaceIdVector.setVal(row, fields[2].getBytes("UTF8"));

      if (!line.contains("NODATA") && !line.contains("SKIPDATA")) {
        srcAddrVector.setVal(row, fields[3].getBytes("UTF8"));
        dstAddrVector.setVal(row, fields[4].getBytes("UTF8"));
        srcPortVector.vector[row] = Integer.parseInt(fields[5]);
        dstPortVector.vector[row] = Integer.parseInt(fields[6]);
        protocolVector.vector[row] = Short.parseShort(fields[7]);
        packetsVector.vector[row] = Integer.parseInt(fields[8]);
        bytesVector.vector[row] = Long.parseLong(fields[9]);
      }

      startVector.vector[row] = Long.parseLong(fields[10]);
      endVector.vector[row] = Long.parseLong(fields[11]);
      actionVector.setVal(row, fields[12].getBytes("UTF8"));
      logStatusVector.setVal(row, fields[13].getBytes("UTF8"));

      if (batch.size == batch.getMaxSize()) {
        writer.addRowBatch(batch);
        batch.reset();
      }
    }

    if (batch.size != 0) { writer.addRowBatch(batch); batch.reset(); }
    writer.close();
  }
}

