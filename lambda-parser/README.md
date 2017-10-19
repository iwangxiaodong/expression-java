gradle/maven - https://bintray.com/wangxiaodong/maven/lambda-parser

jOOQ logo remove - in org.jooq.Log.Level.supports(...) add:
            if (level.equals(this.INFO)) {
                return false;
            }