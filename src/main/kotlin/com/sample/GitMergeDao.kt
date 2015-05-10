package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.revwalk.RevCommit
import kotlin.platform.platformStatic

internal object GitMergeDao {
	/**
	 * マージします。
	 *
	 * もしコンフリクトしたら、対象ブランチをコンフリクト前に戻してエラーメッセージを返します。
	 *
	 * もしbranchが見つからなければ同様にエラーメッセージを返します。
	 *
	 * @return 成功なら左がnullで右側にUnitを, 失敗なら左側にエラーメッセージで右にnullを返します。
	 */
	platformStatic fun merge(git: Git, targetRevCommit: RevCommit, toBranchName:String):Pair<String?, Unit?> {
		val ref = git.checkout().setName(toBranchName).call()
		if(ref.getName() != toBranchName){
			return Pair("checkOut error. target:$toBranchName, actual ${ref.getName()}", null)
		}
		val mResult = git.merge().include(targetRevCommit).call()
		if(!mResult.getMergeStatus().isSuccessful()){
			mResult.getConflicts().forEach { v ->  println(v)}
			git.reset().setMode(ResetCommand.ResetType.HARD).setRef("HEAD").call()
			return Pair("conflict error: " + mResult.getConflicts().keySet().join(","), null)
		}else{
			return Pair(null, Unit)
		}
	}
}