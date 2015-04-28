package com.sample

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.revwalk.RevWalk
import kotlin.platform.platformStatic

data class GitDiffDto(oldPath:String,
                      newPath:String,
                      changeType:String)