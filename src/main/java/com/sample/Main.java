package com.sample;


import kotlin.Pair;
import kotlin.Unit;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class Main {

	public static void main(String[] args){
		String targetPath = "/home/shiba/Desktop/testRepo5";
		String remotePath = "http://argon:8081/git/shibata_ko/TestRepo.git";
		String user = "root";
		String pass = "root";
		CredentialsProvider cp = new UsernamePasswordCredentialsProvider(user, pass);

		Git git = GitRepositoryDao.getGitObject(targetPath, remotePath, cp);
		RevCommit oldCommit = GitCommitDao.getRevCommit(git, "152ff34d56316fef0550445a94d4a46d393cd938");
		RevCommit newCommit = GitCommitDao.getRevCommit(git, "d96ecc4880a0b8625e55b604ca167321c664a083");
		GitCommitDao.getDiff(git, oldCommit, newCommit);
		GitBranchDao.getList(git).forEach(v -> System.out.println(v));
//		RevCommit head = GitBranchDao.getHeadRef(git, "refs/remotes/origin/moke");
//		Pair<String, Unit> pair = GitMergeDao.merge(git, head, "refs/heads/master");
//		if(pair.component1() != null){
//			System.out.println(pair.component1());
//		}
		Pair<String, Unit> res = GitPushDao.push(git, "refs/heads/master", cp);
		if(res.component1() != null){
			System.out.println("error");
			System.out.println(res.component1());
		}else{
			System.out.println("success");
		}
	}
}
