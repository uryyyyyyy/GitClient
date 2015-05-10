package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.platform.platformStatic

object Util {
	platformStatic fun externalCommandExec(vararg strs : String):String {
		val pb = ProcessBuilder(*strs)
		val process = pb.start()

		process.waitFor()
		val is_ = process.getInputStream()
		val br = BufferedReader(InputStreamReader(is_))
		val sb = StringBuilder()
		br.forEachLine { v->
			sb.append(v)
			sb.append(System.lineSeparator())
		}
		return sb.toString()
	}

	platformStatic fun findRevCommit(git: Git, commitHash : String):RevCommit {
		val walk = RevWalk(git.getRepository())
		val objectId = git.getRepository().resolve(commitHash)
		return walk.parseCommit(objectId)
	}
}