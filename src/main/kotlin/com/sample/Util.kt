package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import java.io.UnsupportedEncodingException
import java.util.Arrays
import kotlin.platform.platformStatic

object Util {

	platformStatic fun externalCommandExec(vararg strs : String):Pair<String, Int> {
		val pb = ProcessBuilder(*strs)
		val process = pb.start()

		process.waitFor()
		val is_ = process.getInputStream()
		val es_ = process.getErrorStream()
		process.waitFor()

		val is_Bytes = is_.readBytes()
		val es_Bytes = es_.readBytes()
		if(is_Bytes.size() > es_Bytes.size()){
			return Pair(byteToStr(is_Bytes), 0)
		}else{
			return Pair(byteToStr(es_Bytes), 1)
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

	platformStatic fun byteToStr(bytes : ByteArray):String {
		return if(isSJIS(bytes)){
			String(bytes, "Shift_JIS")
		}else if(isMS932(bytes)){
			String(bytes, "MS932")
		}else if(isUTF8(bytes)){
			String(bytes, "UTF8")
		}else{
			println("nothing")
			""
		}
	}

	platformStatic fun isUTF8(bytes : ByteArray):Boolean {
		try{
			val tmp = String(bytes, "UTF8").getBytes("UTF8")
			return Arrays.equals(tmp, bytes);
		}catch(e:UnsupportedEncodingException) {
			println("UnsupportedEncoding: UTF8")
			return false;
		}
	}

	platformStatic fun isSJIS(bytes : ByteArray):Boolean {
		try{
			val tmp = String(bytes, "Shift_JIS").getBytes("Shift_JIS")
			return Arrays.equals(tmp, bytes);
		}catch(e:UnsupportedEncodingException) {
			println("UnsupportedEncoding: Shift_JIS")
			return false;
		}
	}

	platformStatic fun isMS932(bytes : ByteArray):Boolean {
		try{
			val tmp = String(bytes, "MS932").getBytes("MS932")
			return Arrays.equals(tmp, bytes);
		}catch(e:UnsupportedEncodingException) {
			println("UnsupportedEncoding: MS932")
			return false;
		}
	}
}