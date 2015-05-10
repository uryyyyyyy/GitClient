package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import java.io.File
import java.io.IOException
import kotlin.platform.platformStatic

object Main {

	platformStatic fun main(args: Array<String>){
		val targetFolder = "/media/shiba/shibaHDD/develop/git/testRepo/"
		val maintenanceBranch = "master"
		val maintenanceHash = "7aa87e634cb78a7c4b36a43eb29acd6063e55a59"
		val topicHash = "99f515124de440190fe9cf70d1b11c6c0d817990"

		val repository: FileRepository = FileRepository(File(targetFolder + Constants.DOT_GIT))
		val git = Git(repository)
		try {
			println("--stash local change--")
			val stash = git.stashCreate().call()
			System.out.println("Created stash " + stash)

			println("--fetch & check targetRepo has correct origin--")
			val fResult = git.fetch().call()
			println(fResult.getURI().toString())

			println("--change branch--")
			git.checkout().setName("refs/heads/$maintenanceBranch").call()

			println("--git-svn rebase--")
			val s1 = Util.externalCommandExec("./shell/gitSvnRebase.sh")
			println(s1)

			println("--git push force(when svn repo was updated)--")
			git.push().setForce(true).call()

			println("--merge--")
			val topicBranch = Util.findBranchNameFromHeadHash(git, topicHash)
			println("merge $maintenanceBranch <- $topicBranch")
			val targetRev = Util.findRevCommit(git, topicHash)
			val mResult = git.merge().include(targetRev).call()
			if (!mResult.getMergeStatus().isSuccessful()) {
				println("Error: conflict happen")
				mResult.getConflicts().forEach { v -> println(v) }
				throw IOException("conflict error: " + mResult.getConflicts().keySet().join(","))
			}

			println("--get author--")
			val authorName = targetRev.getAuthorIdent().getName()
			println(authorName)

			println("--git-svn dcommit--")
			val s2 = Util.externalCommandExec("./shell/gitSvnDcommit.sh", authorName)
			println(s2)

			println("--git push force(update git-svn_dcommit data)--")
			git.push().setForce(true).call()

			println("Success: all done")
		}catch(e:Exception){
			e.printStackTrace()
			println("--reset hard--")
			git.reset().setMode(ResetCommand.ResetType.HARD).setRef(maintenanceHash).call()
		}
	}
}