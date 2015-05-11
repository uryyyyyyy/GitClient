
## これは何？

メインリポジトリはsvnなんだけど、開発時は部分的にgitサーバー（gitlabなど）を使いたい。

そんなときに使う。


## 構成

- ツールのログを記録しておく何か（jenkins）
- gitのホストサーバー（gitlab）
- svnサーバー（trac）
- git-svnを置いておくところ

これらを用意する。

## 前提条件

- svnのアカウントは、ユーザー名とパスワードが同じで、ユーザー名は自分のgitConfigのuser.nameの値と同じとする。
	- 違う場合は適宜直す。ただしパスワードをこのツールで扱う必要がある。

- git-svnのgitのリモートはsshで接続されていて、そのアカウントはforcePushの権限を持っている。


## 初期設定

まず、git-svnを置きたいところで、svnリポジトリの特定のフォルダを以下のように取得する。
（オプションは各自で編集。prefixはsvn/推奨）

```
git-svn clone  -T trunk -t tags -b branches --prefix svn/ <http://svn/repo/hoge>
```

あるいは

```
git svn init  -T trunk -t tags -b branches --prefix svn/ <http://svn/repo/hoge>`
git svn fetch
```

次に、gitlabなどで用意したgitリポジトリ（ssh）をoriginに登録する。

（事前にsshKeyの登録が必要。このアカウントはforcePushの権限を持っている必要がある。）

```
git remote add origin <git://svn/repo/hoge.git>
```

次に各ブランチをgitサーバー（gitlab）に登録する。
ロジックが決め打ちなため、以下のコマンドを遵守すること。

（masterを触らないほうがロジックがシンプルになるので、trunkも登録する。
tagはsvnで管理されてるなら開発中は必要ないはず。）


```
git checkout svn/<branch>
git checkout -b <branch>
git push origin <branch>
```


## ツールの引数

`./gradlew run -Pargs="<svnPrefix> <targetFolder> <maintenanceBranch> <topicHash>"`

入力例

`./gradlew run -Pargs="svn/ /home/uryyyyyyy/gitSvnTest/ trunk 8e80f03163c16742ee820297117de7a61370fee6"`


## memo

コンフリクトを修正してマージした場合、過去のコミットがsvnの状態と反する差分になるためsvnで弾かれてしまう。
その場合は、現在の保守ブランチから新しいトピックブランチを切って、
コンフリクトしないように再度commitし直す必要がある。
