package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File
import java.io.IOException
import kotlin.platform.platformStatic

object GitRepositoryDao {

	platformStatic fun getGitObject(targetPath:String, remoteUrl:String, cp: CredentialsProvider): Git {
		try {
			println("clone repository from remote")
			return Git.cloneRepository()
					.setCredentialsProvider(cp)
					.setURI(remoteUrl)
					.setDirectory(File(targetPath))
					.call()
		}catch(e: JGitInternalException){
			println("you already have file or folder")
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
}