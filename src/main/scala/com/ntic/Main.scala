package com.ntic

import com.ntic.service.{AggMode, Aggregator, GroupKey}
import com.ntic.config.ConfigLoader
import com.ntic.io.{CsvParser, CsvWriter, FileLister}

import scala.collection.Seq

object Main {
  def main(args: Array[String]): Unit = {
    val conf = ConfigLoader.load()

    println(s"[INFO] Leyendo ficheros de: ${conf.input.dir}")
    val files = FileLister.listFiles(conf.input.dir, conf.input.pattern)
    if (files.isEmpty) {
      println(s"[WARN] No se encontraron ficheros en ${conf.input.dir} con patrón ${conf.input.pattern}")
      sys.exit(0)
    }

    val allTx = files.flatMap { f =>
      val txs = CsvParser.readFile(f, conf.input.delimiter, conf.input.header)
      println(s"[INFO] ${f}: ${txs.size} transacciones válidas")
      txs
    }

    println(s"[INFO] Total transacciones: ${allTx.size}")

    val groupKey = GroupKey.fromString(conf.transform.groupBy)
    val aggMode = AggMode.fromString(conf.transform.aggregation)

    val aggregated = Aggregator.aggregate(allTx, groupKey, aggMode)

    val headerOpt =
      if (conf.output.header) Some(s"${conf.transform.groupBy},${conf.transform.aggregation}(${conf.transform.unit})")
      else None

    val rows: Seq[Seq[String]] =
      aggregated.map { case (k, metric) => Seq(k, metric.bigDecimal.toPlainString) }

    CsvWriter.write(conf.output.file, headerOpt, rows)

    println(s"[INFO] Escrito: ${conf.output.file}")
  }
}
