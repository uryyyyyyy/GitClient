package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import kotlin.platform.platformStatic

internal object GitBranchDao {
	platformStatic fun getList(git: Git): List<String> {
		return git.branchList()
				.setListMode(ListBranchCommand.ListMode.ALL)
				.call()
				.map { v -> v.getName() }
	}

	platformStatic fun getHeadRef(git: Git, branchName:String): RevCommit {
		val hash = git.getRepository().getRef(branchName).getName()
		return GitCommitDao.getRevCommit(git, hash)
	}

	platformStatic fun createBranch(git: Git, fromCommit:RevCommit, newBranchName:String): Ref {
		return git.branchCreate().setStartPoint(fromCommit).setName(newBranchName).call()
	}

	platformStatic fun deleteBranch(git: Git, deleteBranchName:String): Unit {
		git.branchDelete().setBranchNames(deleteBranchName).call()
	}
}