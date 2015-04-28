package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.transport.CredentialsProvider
import java.io.File
import java.io.IOException
import kotlin.platform.platformStatic

internal object GitRepositoryDao {


    /**
     * gitオブジェクトを取得する。以降はこいつを操作することになる。
     *
     * 引数によって以下の分岐をする。
     *
     * targetPathに,remoteUrlをoriginに持つgitリポジトリが既にある場合は、fetchしてそれを返す。
     *
     * targetPathに,既に関係のないファイルが存在する場合はエラーを返す。(originにremoteUrlを持たない場合も同様。)
     *
     * targetPathに,ファイルが存在しない場合は新規にcloneしてくる。
     *
     * また、cloneする場合はcpに格納されたユーザーとして行う。
     */
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