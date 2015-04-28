package com.sample

import org.eclipse.jgit.api.Git
import java.io.File

fun main(args: Array<String>) {
    val REMOTE_URL = "https://github.com/github/testrepo.git";
    // prepare a new folder for the cloned repository
    val localPath = File.createTempFile("TestGitRepository", "");
    localPath.delete();

    // then clone
    System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
    val git = Git.cloneRepository()
            .setURI(REMOTE_URL)
            .setDirectory(localPath)
            .call();

    try {
        // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
        System.out.println("Having repository: " + git.getRepository().getDirectory());
    } finally {
        git.close();
    }

    //    // ローカルリポジトリの指定など
    //    val builder = FileRepositoryBuilder()
    //    val repository = builder.setGitDir(File("~/ika/" + Constants.DOT_GIT)).readEnvironment().findGitDir().build()
    //
    //    // Git オブジェクト作成 (このオブジェクトを操作していろいろする)
    //    val git = Git(repository)
    //
    //    // ブランチの切り替え(リモートにあるやつ)
    //    try {
    //        // 初めてリモートリポジトリを checkout する場合
    //        git.checkout().setCreateBranch(true).setName("ブランチ名")
    //                .setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.SET_UPSTREAM)
    //                .setStartPoint("リポジトリ名" + "ブランチ名").call(); // origin/ブランチ名 みたいに設定する
    //
    //    } catch (e:RefAlreadyExistsException) {
    //        // 2回目以降(checkout 済みだと上記例外が投げられるっぽいので)
    //        try {
    //            git.checkout().setName("ブランチ名").call();
    //            git.pull().call();
    //        } catch (e1:GitAPIException) {
    //            throw RuntimeException(e);
    //        }
    //    } catch (e:GitAPIException) {
    //        throw RuntimeException(e);
    //    }
    //
    //    // ブランチ名の一覧取得(Ref の getName()で「refs/remotes/origin/master」のように取得できる)
    //    val branchList:List<Ref> = git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
    //
}