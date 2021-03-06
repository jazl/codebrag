package com.softwaremill.codebrag.repository

import com.softwaremill.codebrag.domain.{CommitsForBranch, MultibranchLoadCommitsResult}
import org.eclipse.jgit.revwalk.{RevWalk, RevCommit}
import org.eclipse.jgit.revwalk.filter.MaxCountRevFilter
import scala.collection.JavaConversions._


trait RepositorySnapshotLoader extends RawCommitsConverter {

  self: Repository =>

  def loadLastKnownRepoState(lastKnownBranchPointers: Map[String, String], perBranchMaxCommitsCount: Int): MultibranchLoadCommitsResult = {
    val commonBranchPointers = rejectNonExistingBranches(lastKnownBranchPointers)
    val commitsForBranches = commonBranchPointers.map { case (branchName, branchKnownTop) =>
      val rawCommits = getOldBranchCommitsUntil(branchName, branchKnownTop, perBranchMaxCommitsCount)
      val commitInfos = toCommitInfos(rawCommits)
      CommitsForBranch(branchName, commitInfos, branchNameToSHA(repo.resolve(branchName)))
    }.toList
    MultibranchLoadCommitsResult(repoName, commitsForBranches)
  }

  private def getOldBranchCommitsUntil(branchName: String, untilSHA: String, commitsCount: Int): List[RevCommit] = {
    val walker = new RevWalk(repo)
    walker.setRevFilter(MaxCountRevFilter.create(commitsCount))
    walker.markStart(walker.parseCommit(repo.resolve(untilSHA)))
    val commits = walker.iterator().toList
    walker.dispose
    logger.debug(s"Got ${commits.size} old commit(s) for branch ${branchName}")
    commits
  }

  private def rejectNonExistingBranches(knownRepoSnapshot: Map[String, String]) = {
    val commonBranches = remoteBranchesFullNames.intersect(knownRepoSnapshot.keySet)
    knownRepoSnapshot.filter { b => commonBranches.contains(b._1) }
  }

}