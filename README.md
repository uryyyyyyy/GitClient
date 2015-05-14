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

### updateスクリプト（gitlabの場合）

/opt/gitlab/embedded/service/gitlab-shell/hooks/update など

gitlabに実行権限を与えておく


```
LOG_PATH=/home/ubuntu/gitlabUpdateLog.txt
echo "gitlab update hook start"  >> LOG_PATH
echo $1 >> LOG_PATH
echo $2 >> LOG_PATH
echo $3 >> LOG_PATH

REVS=`git rev-list --parents -n 1 $3` >> /home/shiba/mm.txt

mainHead=`echo $REVS | cut -c 42-81`
topicHead=`echo $REVS | cut -c 83-122`
    
echo "revs"  >> LOG_PATH
echo $REVS >> LOG_PATH
echo $mainHead >> LOG_PATH
echo $topicHead >> LOG_PATH
    
if [ -n "$topicHead" ]; then
   curl -X POST http://172.27.51.24:8081/job/kicker/buildWithParameters?topicHead=$topicHead
fi
echo "gitlab update hook end" >> LOG_PATH

```

### jenkinsのジョブ設定例

フリージョブ


```

cd /home/shiba/Desktop/gitSvnHelper/
./gradlew run -Pargs="svn/ /home/uryyyyyyy/gitSvnTest/ trunk $topicHash"
```


## ツールのビルド

./gradlew clean fatJar

## ツールの実行

`java -jar ./build/libs/gitSvnTool.jar <svnPrefix> <targetFolder> <maintenanceBranch> <topicHash>`

入力例

`java -jar ./build/libs/gitSvnTool.jar svn/ /home/uryyyyyyy/gitSvnTest/ trunk 8e80f03163c16742ee820297117de7a61370fee6`


## memo

コンフリクトを修正してマージした場合、過去のコミットがsvnの状態と反する差分になるためsvnで弾かれてしまう。
その場合は、現在の保守ブランチから新しいトピックブランチを切って、
コンフリクトしないように再度commitし直す必要がある。
