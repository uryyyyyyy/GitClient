package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevWalk
import java.io.File
import kotlin.platform.platformStatic

object GitClientDao {
    platformStatic fun check() {
        val targetPath = "/home/shiba/samplea"

        val git = getGit(targetPath)
        println(git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call())
        val HEAD = git.getRepository().getRef("refs/heads/master");
        git.checkout().setName("refs/remotes/origin/blue").call()
        //git.pull().setRemoteBranchName("blue").call()
        git.fetch().call()
        git.merge().include(HEAD).call()

        val repository = git.getRepository()
        val diffFormatter = DiffFormatter(System.out);
        diffFormatter.setRepository(repository);
        val walk = RevWalk(repository);
        val fromCommit = walk.parseCommit(repository.resolve("78cf42b3249a69c0602b8bcb074cb6a61156787f"));
        val toCommit = walk.parseCommit(repository.resolve("040b4674e7f9480f61f33441964b66c044dbc47f"));
        val fromTree = fromCommit.getTree();
        val toTree = toCommit.getTree();
        val list = diffFormatter.scan(toTree, fromTree);
        list.forEach{ diffEntry:DiffEntry ->
            System.out.printf("%s\t%s\n",
                    diffEntry.getChangeType(),
                    diffEntry.getNewPath());
        }
        walk.dispose();
        println("done")

    }

    platformStatic private fun getGit(targetPath:String):Git {
        val REMOTE_URL = "https://github.com/github/testrepo.git"
        // prepare a new folder for the cloned repository
        try {
            return Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(File(targetPath))
                    .call()
        }catch(e: JGitInternalException){
            println("already exist")
            val repository: FileRepository = FileRepository(File(targetPath + "/" + Constants.DOT_GIT))
            return Git(repository)
        }
    }
}