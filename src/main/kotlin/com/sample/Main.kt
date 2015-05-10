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
		val topicHash = "ffe3a9da7e1aaf3113570cd7107cfbbdb8103a86"

		val repository: FileRepository = FileRepository(File(targetFolder + Constants.DOT_GIT))
		val git = Git(repository)
		try {
			git.branchList().call().forEach { v -> println(v) }
			println("--change branch--")
			git.checkout().setName("refs/heads/$maintenanceBranch").call()

			println("--stash local change--")
			val stash = git.stashCreate().call()
			System.out.println("Created stash " + stash)

			println("--fetch & check targetRepo has correct origin--")
			val fResult = git.fetch().call()
			println(fResult.getURI().toString())

			println("--git-svn rebase--")
			val s1 = Util.externalCommandExec("./shell/gitSvnRebase.sh")
			println(s1)

			println("--merge--")
			val targetRev = Util.findRevCommit(git, topicHash)
			val mResult = git.merge().include(targetRev).call()
			if (!mResult.getMergeStatus().isSuccessful()) {
				mResult.getConflicts().forEach { v -> println(v) }
				throw IOException("conflict error: " + mResult.getConflicts().keySet().join(","))
			}

			println("--get author--")
			val authorName = targetRev.getAuthorIdent().getName()
			println(authorName)

			println("--git-svn dcommit--")
			val s2 = Util.externalCommandExec("./shell/gitSvnDcommit.sh")
			println(s2)

			println("--git push force--")
			git.push().setForce(true).call()

		}catch(e:Exception){
			println("--reset hard--")
			git.reset().setMode(ResetCommand.ResetType.HARD).setRef(maintenanceHash).call()
			e.printStackTrace()
		}
	}
}