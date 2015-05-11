package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import java.io.File
import java.io.IOException
import kotlin.platform.platformStatic

object Main {

	platformStatic fun main(args: Array<String>){
		val svnPrefix = "svn/"
		val targetFolder = "/home/shiba/Desktop/gitsvn2/gitSvnTest/"//args[0]//"/media/shiba/shibaHDD/develop/git/testRepo/"
		val maintenanceBranch = "trunk"//args[1]//"master"
		val topicHash = "8e80f03163c16742ee820297117de7a61370fee6"//args[2]//"99f515124de440190fe9cf70d1b11c6c0d817990"

		val repository: FileRepository = FileRepository(File(targetFolder + Constants.DOT_GIT))
		val git = Git(repository)
		val maintenanceHash = git.getRepository().getRef("refs/heads/$maintenanceBranch").getObjectId().getName()
		println("mainHash: $maintenanceHash")
		try {
			println("--stash local change--")
			val stash = git.stashCreate().setRef(maintenanceHash).call()
			System.out.println("Created stash " + stash)

			println("--fetch & check targetRepo has correct origin--")
			val fResult = git.fetch().call()
			println(fResult.getURI().toString())

			println("--change branch(svn branch)--")
			git.checkout().setName("refs/remotes/$svnPrefix$maintenanceBranch").call()

			println("--get author--")
			val targetRev = Util.findRevCommit(git, topicHash)
			val authorName = targetRev.getAuthorIdent().getName()
			println(authorName)

			println("--git-svn rebase--")
			val s1 = Util.externalCommandExec("./shell/gitSvnRebase.sh", targetFolder, authorName, authorName)
			if(s1.second != 0){
				println("Error: in this git-svn repo, $maintenanceBranch branch status is something wrong")
				s1.first.forEach { v ->  println(v)}
				throw IOException("shell Error: ")
			}
			s1.first.forEach { v ->  println(v)}

			println("--git push force(when svn repo was updated)--")
			git.branchDelete().setBranchNames(maintenanceBranch).setForce(true).call()
			git.branchCreate().setName(maintenanceBranch).call()
			git.checkout().setName("refs/heads/$maintenanceBranch").call()
			try{
				git.push().setForce(true).call()
			}catch(e: TransportException){
				println(e.getMessage())
			}
			git.checkout().setName("refs/remotes/$svnPrefix$maintenanceBranch").call()

			println("--merge--")
			val topicBranch = Util.findBranchNameFromHeadHash(git, topicHash)
			println("merge $maintenanceBranch <- $topicBranch")
			val mResult = git.merge().include(targetRev).call()
			if (!mResult.getMergeStatus().isSuccessful()) {
				println("Error: conflict happen")
				mResult.getConflicts().forEach { v -> println(v) }
				throw IOException("conflict error: " + mResult.getConflicts().keySet().join(","))
			}

			println("--git-svn dcommit--")
			val s2 = Util.externalCommandExec("./shell/gitSvnDcommit.sh", targetFolder, authorName, authorName)
			if(s2.second != 0){
				println("Error: svn reject your commit. check your commit [merge old commit] or [svn hook]")
				s2.first.forEach { v ->  println(v)}
			}
			s2.first.forEach { v ->  println(v)}

			println("--git push force(update git-svn_dcommit data)--")
			git.branchDelete().setBranchNames(maintenanceBranch).setForce(true).call()
			git.branchCreate().setName(maintenanceBranch).call()
			git.checkout().setName("refs/heads/$maintenanceBranch").call()
			try{
				git.push().setForce(true).call()
			}catch(e: TransportException){
				println(e.getMessage())
			}

			println("Success: all done")
		}catch(e:Exception){
			e.printStackTrace()
			println("--reset hard--")
			git.reset().setMode(ResetCommand.ResetType.HARD).setRef(maintenanceHash).call()
			Util.externalCommandExec("./shell/rmGitSvnRebaseApply.sh", targetFolder)
		}
	}
}