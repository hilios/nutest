package services

import java.io.{File, FileWriter}

import play.api.libs.Files.TemporaryFile

import scala.io.Source

/**
  * Companion object to load and write to invites.
  */
object InviteService {

  lazy val defaultFile = {
    val file = new File("./invites.txt")
    file.setWritable(true)

    if (! file.exists()) {
      file.createNewFile()
    }

    file
  }

  /**
    * Returns the list of invitations
    */
  def get() = parse(defaultFile)

  /**
    * Saves a invitation file, overwriting old ones.
    *
    * @param file The invitation file
    * @return The update list
    */
  def save(file: TemporaryFile) = {
    file.moveTo(defaultFile, true)
    defaultFile.setWritable(true)
    get()
  }

  /**
    * Add new lines to the invitation file.
    *
    * @param invites A list of invitations
    * @return The update list
    */
  def add(invites: Seq[String]) = {
    val fw = new FileWriter(defaultFile, true)
    invites.foreach(line => fw.write(s"$line\n"))
    fw.flush()
    fw.close()
    get()
  }

  /**
    * Returns some file as list of invitation in the format (From, To).
    *
    * @param file The file to parse
    * @return A list of invitations
    */
  def parse(file: File): Seq[(Int, Int)] = load(file) map(_.split(" ") match {
    case Array(from, to, other @ _*) => (from.toInt, to.toInt)
  })

  /**
    * Returns each line of some file as an item of a list, if no file is given try to open the
    * default.
    *
    * @param file The file to load
    * @return A list of string
    */
  def load(file: File): Seq[String] = Source.fromFile(file).getLines.filterNot(_.isEmpty).toList
}
