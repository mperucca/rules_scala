load("@rules_scala_annex//rules:scala.bzl", "configure_zinc_scala", "scala_library")

scala_library(
    name = "kind-projector",
    srcs = ["KindProjector.scala"],
    plugins = [
        "@annex_test//:org_typelevel_kind_projector_2_13_8",
    ],
    tags = ["manual"],
)

configure_zinc_scala(
    name = "scala_2_13_8_with_kind_projector",
    compiler_bridge = "@annex//:org_scala_sbt_compiler_bridge_2_13",
    compiler_classpath = [
        "@annex//:org_scala_lang_scala_compiler_2_13_8",
        "@annex//:org_scala_lang_scala_reflect_2_13_8",
        "@annex//:org_scala_lang_scala_library_2_13_8",
    ],
    global_plugins = [
        "@annex_test//:org_typelevel_kind_projector_2_13_8",
    ],
    runtime_classpath = [
        "@annex//:org_scala_lang_scala_library_2_13_8",
    ],
    version = "2.13.8",
)

scala_library(
    name = "kind-projector-via-global-plugins",
    srcs = ["KindProjector.scala"],
    scala = ":scala_2_13_8_with_kind_projector",
    tags = ["manual"],
)
