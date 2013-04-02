package com.softwaremill.codebrag.dao

import pl.softwaremill.common.util.RichString
import com.softwaremill.codebrag.domain.{CommitFileInfo, CommitInfo}
import org.joda.time.DateTime
import org.bson.types.ObjectId
import ObjectIdTestUtils._

/**
 * Test utility to easily build commits.
 */
object CommitInfoBuilder {

  val EmptyListOfComments = List.empty

  val EmptyListOfFiles = List.empty

  def createRandomCommit(): CommitInfo = createRandomCommit(new ObjectId())

  def createRandomCommit(number: Long): CommitInfo = createRandomCommit(oid(number))

  def createRandomCommit(id: ObjectId): CommitInfo = {
    val sha = RichString.generateRandom(10)
    val message = RichString.generateRandom(10)
    val authorName = RichString.generateRandom(10)
    val committerName = RichString.generateRandom(10)
    val parent = RichString.generateRandom(10)
    CommitInfo(id, sha, message, authorName, committerName, new DateTime(), List(parent), EmptyListOfFiles)
  }

  def createRandomCommitWithFiles(files: List[CommitFileInfo]): CommitInfo = {
    createRandomCommit().copy(files = files)
  }

}
