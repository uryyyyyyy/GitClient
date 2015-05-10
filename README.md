
## これは何？

メインリポジトリはsvnなんだけど開発時はgitlabなどgitのサーバーを使いたい。

そんなときに使う。


## 構成

- ツールのログを表示する何か（jenkins）
- gitのホストサーバー（gitlab）
- svnサーバー（trac）

これらを用意する。

まず、git-svnを置きたいところで、svnリポジトリの特定のフォルダを以下のように落とす。

```
git-svn clone <http://svn/repo/hoge>
```

あるいは

```
git svn init <http://svn/repo/hoge>`
git svn fetch
```

次に、

- git-svnリポジトリを置くところ


## このツールを動かすための前提条件

- svnのアカウントは、ユーザー名とパスワードが同じで、ユーザー名は自分のgitConfigのuser.nameの値と同じとする。
	- 違う場合は適宜直す。ただしパスワードは必須。
- 事前にgit-svnリポジトリが用意されているものとする。（上記に記載）

- git-svnのgitのリモートはsshで接続されていて、そのアカウントはforcePushの権限を持っているとする。
