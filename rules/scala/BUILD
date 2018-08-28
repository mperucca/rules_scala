load("//rules:scala.bzl", "annex_scala_library", "annex_scala_binary", "annex_scala_deps_toolchain", "annex_scala_runner_toolchain")
load("//rules:scalac.bzl", "scalac_binary", "scalac_library")
load("//rules:scalafmt.bzl", "annex_scala_format_test")
load("//rules:scala.bzl", "scala_import")

# Bloop

scalac_binary(
    name = "bloop",
    srcs = glob(["bloop/**/*.scala"]),
    main_class = "annex.BloopRunner",
    scala = "//external:scala_annex_scala_basic",
    visibility = ["//visibility:public"],
    deps = [
        ":compiler",
        "//rules/common:worker",
        "//rules/common/third_party/bazel/src/java_tools/buildjar/java/com/google/devtools/build/buildjar/jarhelper",
        "@scala_annex_ch_epfl_scala_bloop_frontend_2_12",
        "@scala_annex_org_scala_sbt_test_interface",
    ],
)

toolchain(
    name = "bloop_toolchain",
    toolchain = ":bloop_toolchain_implementation",
    toolchain_type = "@rules_scala_annex//rules/scala:runner_toolchain_type",
    visibility = ["//visibility:public"],
)

annex_scala_runner_toolchain(
    name = "bloop_toolchain_implementation",
    runner = ":bloop",
    visibility = ["//visibility:public"],
)

# Common

toolchain(
    name = "config_runner_toolchain",
    toolchain = ":config_toolchain_implementation",
    toolchain_type = "@rules_scala_annex//rules/scala:runner_toolchain_type",
    visibility = ["//visibility:public"],
)

annex_scala_runner_toolchain(
    name = "config_toolchain_implementation",
    encoding = "UTF-8",
    runner = select({
        ":config_use_bloop": "@rules_scala_annex//rules/scala:bloop",
        "//conditions:default": "@rules_scala_annex//rules/scala:zinc",
    }),
    visibility = ["//visibility:public"],
)

config_setting(
    name = "config_use_bloop",
    values = {
        "define": "use_bloop=true",
    },
)

scalac_library(
    name = "compiler",
    srcs = glob(["compiler/**/*.scala"]),
    scala = "//external:scala_annex_scala_basic",
    deps = [
        "@scala_annex_net_sourceforge_argparse4j_argparse4j",
    ],
)

toolchain_type(
    name = "runner_toolchain_type",
    visibility = ["//visibility:public"],
)

# Deps

toolchain_type(
    name = "deps_toolchain_type",
    visibility = ["//visibility:public"],
)

config_setting(
    name = "config_deps_used_error",
    define_values = {
        "scala_deps_used": "error",
    },
)

config_setting(
    name = "config_deps_used_off",
    define_values = {
        "scala_deps_used": "off",
    },
)

config_setting(
    name = "config_deps_direct_error",
    define_values = {
        "scala_deps_direct": "error",
    },
)

config_setting(
    name = "config_deps_direct_off",
    define_values = {
        "scala_deps_direct": "off",
    },
)

annex_scala_deps_toolchain(
    name = "config_deps_toolchain_implementation",
    direct = select({
        ":config_deps_direct_error": "error",
        ":config_deps_direct_off": "off",
        "//conditions:default": "check",
    }),
    runner = ":deps",
    used = select({
        ":config_deps_used_error": "error",
        ":config_deps_used_off": "off",
        "//conditions:default": "check",
    }),
    visibility = ["//visibility:public"],
)

toolchain(
    name = "config_deps_toolchain",
    toolchain = ":config_deps_toolchain_implementation",
    toolchain_type = "@rules_scala_annex//rules/scala:deps_toolchain_type",
    visibility = ["//visibility:public"],
)

scalac_binary(
    name = "deps",
    srcs = glob(["deps/**/*.scala"]),
    main_class = "annex.deps.DepsRunner",
    scala = "//external:scala_annex_scala_basic",
    visibility = ["//visibility:public"],
    deps = [
        "//rules/common:worker",
        "@scala_annex_net_sourceforge_argparse4j_argparse4j",
    ],
)

# Test

annex_scala_binary(
    name = "test",
    srcs = glob(["test/**/*.scala"]),
    main_class = "annex.TestRunner",
    scala = "//external:scala_annex_scala",
    visibility = ["//visibility:public"],
    deps = [
        "@scala_annex_com_trueaccord_scalapb_scalapb_runtime_2_12",
        "@scala_annex_net_sourceforge_argparse4j_argparse4j",
        "@scala_annex_org_scala_sbt_compiler_interface",
        "@scala_annex_org_scala_sbt_test_interface",
        "@scala_annex_org_scala_sbt_zinc_apiinfo_2_12",
        "@scala_annex_org_scala_sbt_zinc_core_2_12",
        "@scala_annex_org_scala_sbt_zinc_persist_2_12",
    ],
)

# Zinc

proto_library(
    name = "zinc_options_proto",
    srcs = glob(["zinc/**/*.proto"]),
)

java_proto_library(
    name = "zinc_options_proto_java",
    deps = [":zinc_options_proto"],
)

###########################333

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_2_12",
    jars = ["local_zinc/zinc/target/scala-2.12/zinc_2.12-1.2.1.jar"],
    deps = [
        ":kevin_local_org_scala_sbt_zinc_classfile_2_12",
        ":kevin_local_ch_epfl_scala_zinc_compile_core_2_12",
        ":kevin_local_org_scala_sbt_zinc_core_2_12",
        ":kevin_local_org_scala_sbt_zinc_ivy_integration_2_12",
        ":kevin_local_org_scala_sbt_zinc_persist_2_12",
        "@scala_annex_scala_2_12_scala_library//jar",
    ]
)

scala_import(
    name = "kevin_local_ch_epfl_scala_zinc_compile_core_2_12",
    jars = ["local_zinc/internal/zinc-compile-core/target/scala-2.12/zinc-compile-core_2.12-1.2.1.jar"],
    deps = [
        "@scala_annex_org_scala_lang_modules_scala_parser_combinators_2_12",
        "@scala_annex_org_scala_sbt_launcher_interface",
        "@scala_annex_org_scala_sbt_util_control_2_12"
    ]
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_classfile_2_12",
    jars = ["local_zinc/internal/zinc-classfile/target-2.12/scala-2.12/zinc-classfile_2.12-1.2.1.jar"],
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_persist_2_12",
    jars = ["local_zinc/internal/zinc-persist/target/scala-2.12/zinc-persist_2.12-1.2.1.jar"],
    deps = [
        "@scala_annex_com_trueaccord_scalapb_scalapb_runtime_2_12",
        "@scala_annex_scala_2_12_scala_library//jar",
        "@scala_annex_org_scala_sbt_sbinary_2_12",
        ":kevin_local_org_scala_sbt_zinc_core_2_12",
    ]
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_ivy_integration_2_12",
    jars = ["local_zinc/internal/zinc-ivy-integration/target/scala-2.12/zinc-ivy-integration_2.12-1.2.1.jar"],
    deps = [
        "@scala_annex_org_scala_sbt_librarymanagement_core_2_12",
    ]
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_core_2_12",
    jars = ["local_zinc/internal/zinc-core/target/scala-2.12/zinc-core_2.12-1.2.1.jar"],
    deps = [
        ":kevin_local_org_scala_sbt_compiler_interface_2_12",
        "@scala_annex_org_scala_sbt_io_2_12",
        "@scala_annex_org_scala_sbt_util_logging_2_12",
        "@scala_annex_org_scala_sbt_util_relation_2_12",
        ":kevin_local_org_scala_sbt_zinc_apiinfo_2_12",
        ":kevin_local_org_scala_sbt_zinc_classpath_2_12",
    ]
)

scala_import(
    name = "kevin_local_org_scala_sbt_compiler_interface_2_12",
    jars = ["local_zinc/internal/compiler-interface/target/compiler-interface-1.2.1.jar"],
    deps = [
        "@scala_annex_org_scala_sbt_util_interface",
    ]
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_apiinfo_2_12",
    jars = ["local_zinc/internal/zinc-apiinfo/target-2.12/scala-2.12/zinc-apiinfo_2.12-1.2.1.jar"],
)

scala_import(
    name = "kevin_local_org_scala_sbt_zinc_classpath_2_12",
    jars = ["local_zinc/internal/zinc-classpath/target-2.12/scala-2.12/zinc-classpath_2.12-1.2.1.jar"],
)

#################################

scalac_binary(
    name = "zinc",
    srcs = glob(["zinc/**/*.scala"]),
    main_class = "annex.zinc.ZincRunner",
    scala = "//external:scala_annex_scala_basic",
    visibility = ["//visibility:public"],
    runtime_deps = [
        "@scala_annex_com_lihaoyi_sourcecode_2_12",
    ],
    deps = [
        ":compiler",
        "//rules/common:worker",
        "//rules/common/third_party/bazel/src/java_tools/buildjar/java/com/google/devtools/build/buildjar/jarhelper",
        "@scala_annex_org_scala_sbt_test_interface",
        ":kevin_local_org_scala_sbt_zinc_2_12",
    ],
)

annex_scala_runner_toolchain(
    name = "zinc_toolchain_implementation",
    runner = ":zinc",
    visibility = ["//visibility:public"],
)

toolchain(
    name = "zinc_runner_toolchain",
    toolchain = ":zinc_toolchain_implementation",
    toolchain_type = "@rules_scala_annex//rules/scala:runner_toolchain_type",
    visibility = ["//visibility:public"],
)

# Other

annex_scala_format_test(
    name = "format",
    srcs = glob(["**/*.scala"]),
)
