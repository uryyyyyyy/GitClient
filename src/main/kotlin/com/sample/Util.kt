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
	platformStatic fun externalCommandExec(vararg strs : String):Pair<String, Int> {
		val bytes = externalCommandExec_(*strs)
		return Pair(String(bytes.first, "UTF-8"), bytes.second)
	}

	platformStatic fun externalCommandExec_(vararg strs : String):Pair<ByteArray, Int> {
		val pb = ProcessBuilder(*strs)
		val process = pb.start()

		process.waitFor()
		val is_ = process.getInputStream()
		val es_ = process.getErrorStream()
		process.waitFor()

		val is_Bytes = is_.readBytes()
		val es_Bytes = es_.readBytes()
		if(is_Bytes.size() > es_Bytes.size()){
			return Pair(is_Bytes, 0)
		}else{
			return Pair(es_Bytes, 1)
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

	platformStatic fun externalCommandExec2(vararg strs : String):Pair<String, Int> {
		val bytes = externalCommandExec_(*strs)
		return Pair(String(bytes.first, "MS932"), bytes.second)
	}
}