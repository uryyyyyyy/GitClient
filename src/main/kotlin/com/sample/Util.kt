package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.ArrayList
import kotlin.platform.platformStatic

object Util {
	platformStatic fun externalCommandExec(vararg strs : String):Pair<List<String>, Int> {
		val pb = ProcessBuilder(*strs)
		val process = pb.start()

		process.waitFor()
		val is_ = process.getInputStream()
		val es_ = process.getErrorStream()
		process.waitFor()

		val list0 = ArrayList<String>()
		val list1 = ArrayList<String>()
		BufferedReader(InputStreamReader(is_)).forEachLine { v->list0.add(v)}
		BufferedReader(InputStreamReader(es_)).forEachLine { v->list1.add(v)}
		if(list0.size() > list1.size()){
			return Pair(list0, 0)
		}else{
			return Pair(list1, 1)
		}
	}

	platformStatic fun findRevCommit(git: Git, commitHash : String):RevCommit {
		val walk = RevWalk(git.getRepository())
		val objectId = git.getRepository().resolve(commitHash)
		return walk.parseCommit(objectId)
	}

	platformStatic fun findBranchNameFromHeadHash(git: Git, commitHash : String):String {
		val allList = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call()
		return allList.filter { v -> v.getObjectId().getName() == commitHash }.first().getName()
	}
}