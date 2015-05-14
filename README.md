## これは何？

メインリポジトリはsvnなんだけど、開発時は部分的にgitサーバー（gitlabなど）を使いたい。

そんなときに使う。


## ツールのビルド

./gradlew clean fatJar

## ツールの実行

`java -jar ./build/libs/gitSvnTool.jar <svnPrefix> <targetFolder> <maintenanceBranch> <topicHash>`

入力例

`java -jar ./build/libs/gitSvnTool.jar svn/ /home/uryyyyyyy/gitSvnTest/ trunk 8e80f03163c16742ee820297117de7a61370fee6`




以下、このツールを具体的にどうやって使うのか記述する。



## 構成

- ツールのログを記録しておく何か（jenkins）
- gitのホストサーバー（gitlab）
- svnサーバー（trac）
- git-svnを置いておくところ

（）の中身はなんでも構わないが、これ以降は（）内のシステムを使った場合の説明をします。

## 前提条件

- svnのアカウントは、ユーザー名とパスワードが同じで、ユーザー名は自分のgitConfigのuser.nameの値と同じとする。
	- 違う場合は適宜直す。ただしパスワードをこのツールで扱う必要がある。


- git-svnのgitのリモートはsshで接続されていて、そのアカウントはforcePushの権限を持っている。


## 初期設定

上から順に設定して下さい。

### GITLAB

公式インストーラからGITLABを構築した場合を想定します。

- 対象プロジェクトを作成します。（まだリポジトリは作らない。）
- push forceできるアカウントを用意します。


### git-svnリポジトリ

まず、svnの情報を取得するgit-svnリポジトリが必要です。
git-svnを置きたいところで以下を実行します。（オプションは各自のsvn構成に合わせて。）

```
git-svn clone  -T trunk -t tags -b branches --prefix svn/ <http://svn/repo/hoge>

#or
#git svn init  -T trunk -t tags -b branches --prefix svn/ <http://svn/repo/hoge>`
#git svn fetch
```

このマシンのsshKeyを、先ほど作成したpush ForceできるアカウントのsshKeyとして登録します。

gitlabなどで用意したgitリポジトリ（sshの方のアドレス）をoriginに登録します。

```
git remote add origin <git://svn/repo/hoge.git>
```

次に各ブランチをgitサーバー（gitlab）に登録します。

（ロジックが決め打ちなため、以下のコマンドを遵守すること。
　masterを触らないほうがロジックがシンプルになるので、trunkも登録する。
　tagは開発に関係ないので使用しない。）


```
git checkout svn/<branch>
git checkout -b <branch>
git push origin <branch>
```

### gitSvnTool

git-svnリポジトリを置いたサーバー内に、gitSvnTool（今ReadMeを見ているこのリポジトリ）を落とす。

ビルドしておく。

`./gradlew clean fatJar`

### jenkins

フリースタイル・プロジェクトでジョブを作成する。

ビルドパラメータを2つ用意する。

- topicHash(マージ対象のトピックブランチのheadのhash)
- maintenanceBranch(保守ブランチ。例：trunk, Ver1.2など)

スクリプトを登録する。

```
cd <gitSvnToolのtopDirectory>

java -jar ./build/libs/gitSvnTool.jar svn/ <git-svnのリポジトリのtopDirectory> $maintenanceBranch $topicHash
```


### GITLAB

登録された各ブランチ（trunk, Ver1.2 etc...）への一般アカウントからのpushを禁止する。
（もちろんmasterも）

GITLABのupdateスクリプトを更新する。(gitlabに実行権限を与えておく。logを出力したい場合logFileにも)

```
sudo mv /opt/gitlab/embedded/service/gitlab-shell/hooks/update /opt/gitlab/embedded/service/gitlab-shell/hooks/update_old
sudo vim /opt/gitlab/embedded/service/gitlab-shell/hooks/update
```

```
LOG_PATH=/home/ubuntu/gitlabUpdateLog.txt
echo "gitlab update hook start"  >> $LOG_PATH
echo $1 >> $LOG_PATH
echo $2 >> $LOG_PATH
echo $3 >> $LOG_PATH

REVS=`git rev-list --parents -n 1 $3`

# maintenanceHash=`echo $REVS | cut -c 42-81`
topicHash=`echo $REVS | cut -c 83-122`
echo "topicHash: $topicHash"  >> $LOG_PATH

maintenanceBranch=`echo $1 | cut -c 12-`
echo "maintenanceBranch: $maintenanceBranch"  >> $LOG_PATH

if [ -n "$topicHash" ]; then
   echo "gitlab merge hook end" >> $LOG_PATH
   curl -X POST "http://<jenkinsのホスト>/job/<jenkinsのジョブ名>/buildWithParameters?maintenanceBranch=$maintenanceBranch&topicHash=$topicHash"
   exit 1
else
   exit 0
fi

```

## 実際の運用

### 通常時

各開発者がGITLABからソースをcloneする。

開発したいブランチから新しくトピックブランチを作成して開発する。

開発が完了したらトピックブランチをpushし、保守ブランチへのPRを出す。

CI、レビューの後、権限を持ったアカウントがそのPRを承認する
（このとき、GITLABに仕掛けたupdateスクリプトが実行される。）
（承認ボタンを押すと処理中っぽいUIになりますが、実際のマージ作業はgit-svn上で行われるので画面は閉じて大丈夫です。）

svnへのコミットがうまくいったかどうかはjenkinsで確認する。
（ログメッセージを漁ればだいたいの情報は取れるはず。）

成功した場合、今回のトピックブランチを削除しブランチをpullしなおせばOK。

### 失敗時

PRを出そうと思ったら保守ブランチの状態が変わっていた。
あるいは、jenkinsのジョブが失敗していた。

このときは、トピックブランチを修正する必要がある。

まず保守ブランチをpullし、トピックブランチで`git rebase <保守ブランチ>`する。
コンフリクトが起きたら各自のローカルで解消する。

そして再度PRを送る。

## memo

svnのエラーメッセージなどが日本語の場合、言語を日本語にしておいてください。

```
apt-get install language-support-ja

# echo "LC_ALL=ja_JP.UTF-8" >> /etc/default/locale
# cat /etc/default/locale
LANG=ja_JP.UTF-8
LC_ALL=ja_JP.UTF-8

shutdown -r now

# check setting
locale
```