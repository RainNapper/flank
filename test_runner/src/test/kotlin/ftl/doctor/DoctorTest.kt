package ftl.doctor

import com.google.common.truth.Truth.assertThat
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.test.util.FlankTestRunner
import org.junit.Test
import org.junit.runner.RunWith
import java.io.StringReader
import java.nio.file.Paths

@RunWith(FlankTestRunner::class)
class DoctorTest {
    @Test
    fun androidDoctorTest() {
        val lint = validateYaml(AndroidArgs, Paths.get("src/test/kotlin/ftl/fixtures/flank.local.yml"))
        assertThat(lint).isEmpty()
    }

    @Test
    fun androidDoctorTest2() {
        val lint = validateYaml(
            AndroidArgs, """
hi: .
foo:
  bar: 1
gcloud:
  results-bucket: .
  record-video: .
  timeout: .
  async: .
  results-history-name: .

  app: .
  test: .
  auto-google-login: .
  use-orchestrator: .
  environment-variables:
    clearPackageData: .
  directories-to-pull:
  - .
  performance-metrics: .
  test-targets:
  - .
  device:
  - model: .
    version: .
    locale: .
    orientation: .
  two: .

flank:
  max-test-shards: 7
  num-test-runs: 8
  test-targets-always-run:
    - .
  three: .
  project: .
        """.trimIndent()
        )
        assertThat(lint).isEqualTo(
            """
Unknown top level keys: [hi, foo]
Unknown keys in gcloud -> [two]
Unknown keys in flank -> [three]

""".trimIndent()
        )
    }

    @Test
    fun androidDoctorTest3() {
        val lint = validateYaml(
            AndroidArgs, """
gcloud:
  app: .
  test: .
flank:
  project: .
        """.trimIndent()
        )
        assertThat(lint).isEqualTo("")
    }

    @Test
    fun androidDoctorTestWithFailedConfiguration() {
        // given
        val expectedErrorMessage = """
Error on parse config: flank->additional-app-test-apks
At line: 20, column: 5
Error node: {
  "additional-app-test-apks" : {
    "app" : "../sample/app/build/output/apk/debug/sample-app.apk",
    "test" : "../library/databases/build/output/apk/androidTest/debug/sample-databases-test.apk"
  }
}
        """.trimIndent()

        // when
        val actual = validateYaml(
            AndroidArgs,
            Paths.get("src/test/kotlin/ftl/fixtures/flank_android_failed_configuration.yml")
        )
        assertThat(actual).isEqualTo(expectedErrorMessage)
    }

    @Test
    fun androidDoctorTestWithFailedTree() {
        // given
        val expectedErrorMessage = """
    Error on parse config: expected <block end>, but found '?'
    At line: 19, column: 4
    Error node: 
            test: ../main/app/build/output/a ... 
            ^Error on parse config: expected <block end>, but found '?'
    At line: 19, column: 4
    Error node: 
            test: ../main/app/build/output/a ... 
            ^
        """.trimIndent()

        // when
        val actual = validateYaml(
            AndroidArgs,
            Paths.get("src/test/kotlin/ftl/fixtures/flank_android_failed_tree.yml")
        )
        assertThat(actual).isEqualTo(expectedErrorMessage)
    }

    @Test
    fun iosDoctorTest() {
        val lint = validateYaml(IosArgs, Paths.get("src/test/kotlin/ftl/fixtures/flank.ios.yml"))
        assertThat(lint).isEmpty()
    }

    @Test
    fun iosDoctorTest2() {
        val lint = validateYaml(
            IosArgs, """
hi: .
foo:
  bar: 1
gcloud:
  results-bucket: .
  record-video: .
  timeout: .
  async: .
  results-history-name: .

  test: .
  xctestrun-file: .
  device:
  - model: .
    version: .
    locale: .
    orientation: .
  two: .

flank:
  max-test-shards: .
  num-test-runs: .
  test-targets-always-run:
    - .
  test-targets:
    - .
  three: .
  project: .
""".trimIndent()
        )
        assertThat(lint).isEqualTo(
            """
Unknown top level keys: [hi, foo]
Unknown keys in gcloud -> [two]
Unknown keys in flank -> [three]

""".trimIndent()
        )
    }

    @Test
    fun iosDoctorTest3() {
        val lint = validateYaml(
            IosArgs, """
gcloud:
  test: .
  xctestrun-file: .
flank:
  project: .
""".trimIndent()
        )
        assertThat(lint).isEqualTo("")
    }
}

private fun validateYaml(args: IArgs.ICompanion, data: String): String = validateYaml(args, StringReader(data))
