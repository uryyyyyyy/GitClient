package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.transport.CredentialsProvider
import kotlin.platform.platformStatic

internal object GitPushDao {
    platformStatic fun push(git: Git, branchName:String, cp:CredentialsProvider):Pair<String?, Unit?> {
        println(git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call())
        val ref = git.checkout().setName(branchName)?.call()
        if(ref?.getName() != branchName){
            return Pair("checkOut error. target:$branchName, actual ${ref?.getName()}", null)
        }
        val pResult = git.push().setCredentialsProvider(cp).call()
        if(pResult.count() != 0){
            val sb = StringBuilder()
            pResult.forEach { r -> println("aa");sb.append(r.getMessages()) }
            return Pair(sb.toString(), null)
        }
        return Pair(null, Unit)
    }
}