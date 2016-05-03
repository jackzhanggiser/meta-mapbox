SUMMARY = "A library for embedding interactive, customizable vector maps."
HOMEPAGE = "https://github.com/mapbox/mapbox-gl-native"

DEPENDS = "\
    boost \
    geojsonvt \
    geometryhpp \
    nunicode \
    protozero \
    qtbase \
    qtlocation \
    qttools-native \
    rapidjson \
    sqlite3 \
    variant \
    virtual/libgl"

SECTION = "libs"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.md;endline=24;md5=a0832c9e25a6100b4753d7f0c3bb5ce1"

S = "${WORKDIR}/git"

SRCREV = "10e522d10b33e5268b619296eae0ccad899809d9"
PV = "1.0.0+git${SRCPV}"

SRC_URI = "\
    git://github.com/mapbox/mapbox-gl-native.git;protocol=https \
    file://config.gypi \
    file://01-use_qt_native_image_decoders.patch"

inherit pkgconfig qmake5_paths

do_configure() {
    deps/run_gyp \
        --depth=${S} ${S}/platform/qt/platform.gyp \
        -I ${WORKDIR}/config.gypi \
        -Dopengl_cflags="" \
        -Dopengl_ldflags="-lGL" \
        -Dgeojsonvt_static_libs="-lgeojsonvt" \
        -Dnunicode_static_libs="-lnu" \
        -Dqt_moc="${OE_QMAKE_PATH_EXTERNAL_HOST_BINS}/moc" \
        -Dqt_rcc="${OE_QMAKE_PATH_EXTERNAL_HOST_BINS}/rcc" \
        -Dqt_core_cflags="`pkg-config --cflags Qt5Core`" \
        -Dqt_core_ldflags="`pkg-config --libs Qt5Core`" \
        -Dqt_gui_cflags="`pkg-config --cflags Qt5Gui`" \
        -Dqt_gui_ldflags="`pkg-config --libs Qt5Gui`" \
        -Dqt_location_cflags="`pkg-config --cflags Qt5Location`" \
        -Dqt_location_ldflags="`pkg-config --libs Qt5Location`" \
        -Dqt_network_cflags="`pkg-config --cflags Qt5Network`" \
        -Dqt_network_ldflags="`pkg-config --libs Qt5Network`" \
        -Dqt_opengl_cflags="`pkg-config --cflags Qt5OpenGL`" \
        -Dqt_opengl_ldflags="`pkg-config --libs Qt5OpenGL`" \
        -Dqt_positioning_cflags="`pkg-config --cflags Qt5Positioning`" \
        -Dqt_positioning_ldflags="`pkg-config --libs Qt5Positioning`" \
        -Dqt_qml_cflags="`pkg-config --cflags Qt5Qml`" \
        -Dqt_qml_ldflags="`pkg-config --libs Qt5Qml`" \
        -Dqt_quick_cflags="`pkg-config --cflags Qt5Quick`" \
        -Dqt_quick_ldflags="`pkg-config --libs Qt5Quick`" \
        -Dsqlite_cflags="`pkg-config --cflags sqlite3`" \
        -Dsqlite_ldflags="`pkg-config --libs sqlite3`" \
        -Dzlib_cflags="`pkg-config --cflags zlib`" \
        -Dzlib_ldflags="`pkg-config --libs zlib`" \
        --generator-output=${S}/build \
        -f make
}

do_compile() {
    oe_runmake -C${S}/build qt-app qt-qml-app
}

do_install() {
    install -d ${D}/${includedir}/mapbox
    cp -r ${S}/platform/qt/include/* ${D}/${includedir}/mapbox

    install -d ${D}/${bindir}
    install -m 755 build/out/Release/qmapboxgl ${D}/${bindir}
    install -m 755 build/out/Release/qquickmapboxgl ${D}/${bindir}

    install -d ${D}/${libdir}
    oe_libinstall -C build/out/Release/lib.target/ libqmapboxgl ${D}${libdir}
}

# FIXME: not including sources on the debug package
# makes it pretty much useless. This hack is needed
# because we are getting errors on the -dbg package
# creation about "canonicalization".
PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"