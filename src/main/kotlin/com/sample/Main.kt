package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import java.io.File
import java.io.IOException
import kotlin.platform.platformStatic

object Main {

	platformStatic fun main(args: Array<String>){
		println("hello")
		val repository: FileRepository = FileRepository(File(targetPath + "/" + Constants.DOT_GIT))
		val git = Git(repository)
		git.stashCreate().call()
		val fResult = git.fetch().call()
		if(fResult.getURI().toString() != remoteUrl){
			throw IOException("this folder is not target repository")
		}else{
			return git
		}
	}
}