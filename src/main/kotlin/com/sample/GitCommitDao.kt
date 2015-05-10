package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import kotlin.platform.platformStatic

internal object GitCommitDao {
	platformStatic fun getRevCommit(git: Git, commitHash:String): RevCommit {
		val walk = RevWalk(git.getRepository())
		val objectId = git.getRepository().resolve(commitHash)
		return walk.parseCommit(objectId)
	}

	platformStatic fun getDiff(git: Git, oldCommit:RevCommit, newCommit:RevCommit):List<GitDiffDto> {
		val oldTree = oldCommit.getTree()
		val newTree = newCommit.getTree()
		val diffFormatter = DiffFormatter(System.out)
		diffFormatter.setRepository(git.getRepository())
		val list = diffFormatter.scan(oldTree, newTree)
		val res = list.map{ d: DiffEntry ->
			println(d)
			GitDiffDto(d.getOldPath(), d.getNewPath(), d.getChangeType().toString())
		}
		println("done")
		return res
	}
}