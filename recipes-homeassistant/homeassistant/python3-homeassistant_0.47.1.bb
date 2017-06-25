SUMMARY = "Open-source home automation platform running on Python 3"
HOMEPAGE = "https://home-assistant.io/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=f4eda51018051de136d3b3742e9a7a40"

HOMEASSISTANT_CONFIG_DIR ?= "${localstatedir}/lib/homeassistant"
HOMEASSISTANT_CONFIG_DIR[doc] = "Configuration directory used by home-assistant."
HOMEASSISTANT_USER ?= "homeassistant"
HOMEASSISTANT_USER[doc] = "User the home-assistent service runs as."

inherit setuptools3 useradd update-rc.d systemd

inherit pypi
SRC_URI[md5sum] = "378a08c090f0884b6ee6acbc37badcda"
SRC_URI[sha256sum] = "39bc4d6d387ce54faff38262419c215b45e8e17672de83ecce60be399a0b0a68"

SRC_URI += "\
    file://homeassistant.service \
    file://homeassistant.init \
    file://0001-remove-typing-it-is-already-included-in-python-3.5.patch \
    "

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "homeassistant"
USERADD_PARAM_${PN} = "--system --home ${HOMEASSISTANT_CONFIG_DIR} \
                       --no-create-home --shell /bin/false \
                       --groups homeassistant,dialout --gid homeassistant ${HOMEASSISTANT_USER}"

INITSCRIPT_NAME = "homeassistant"

SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE_${PN} = "homeassistant.service"

do_install_append () {
    install -d -o ${HOMEASSISTANT_USER} -g homeassistant ${D}${HOMEASSISTANT_CONFIG_DIR}

    # Install init scripts and set correct config directory
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${WORKDIR}/homeassistant.init  ${D}${sysconfdir}/init.d/homeassistant
    sed -i -e 's,@HOMEASSISTANT_CONFIG_DIR@,${HOMEASSISTANT_CONFIG_DIR},g'  ${D}${sysconfdir}/init.d/homeassistant
    sed -i -e 's,@HOMEASSISTANT_USER@,${HOMEASSISTANT_USER},g'  ${D}${sysconfdir}/init.d/homeassistant

    # Install systemd unit files and set correct config directory
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/homeassistant.service ${D}${systemd_unitdir}/system
    sed -i -e 's,@HOMEASSISTANT_CONFIG_DIR@,${HOMEASSISTANT_CONFIG_DIR},g' ${D}${systemd_unitdir}/system/homeassistant.service
    sed -i -e 's,@HOMEASSISTANT_USER@,${HOMEASSISTANT_USER},g' ${D}${systemd_unitdir}/system/homeassistant.service
}

# Home Assistant core
RDEPENDS_${PN} = " \
    python3-requests (= 2.14.2) \
    python3-pyyaml (>= 3.11)  \
    python3-pytz (>= 2017.02) \
    python3-pip (>= 7.1.0) \
    python3-jinja2 (>= 2.9.5) \
    python3-voluptuous (= 0.10.5) \
    python3-typing (>= 3) \
    python3-aiohttp (= 2.1.0)\
    python3-async-timeout (= 1.2.1) \
    python3-chardet (= 3.0.2) \
    python3-astral (= 1.4) \
    \
    python3-asyncio \
    python3-multiprocessing \
    python3-sqlite3 \
    python3-html \
    "


# Component dependencies
RDEPENDS_${PN} += " \
    python3-aiohttp-cors (= 0.5.3) \
    python3-aiolifx (= 0.4.8) \
    python3-colorlog (>= 2.10.0) \
    python3-distro (= 1.0.4) \
    python3-fuzzywuzzy (= 0.15.0) \
    python3-google-api-python-client (= 1.6.2) \
    python3-gtts-token (= 1.1.1) \
    python3-hbmqtt (= 0.8) \
    python3-knxip (= 0.3.3) \
    python3-libpurecoollink (= 0.1.5) \
    python3-liffylights (= 0.9.4)  \
    python3-lnetatmo (= 0.9.2) \
    python3-mutagen (= 1.37) \
    python3-mystrom (= 0.3.8) \
    python3-netdisco (= 1.0.1) \
    python3-oauth2client (= 4.0.0)\
    python3-paho-mqtt (= 1.2.3) \
    python3-phue (= 0.9) \
    python3-pycec (= 0.4.13) \
    python3-pytradfri (= 1.1) \
    python-restrictedpython (= 4.0a3) \
    python3-soco (= 0.12) \
    python3-sqlalchemy (>= 1.1.10) \
    python3-xmltodict (= 0.11.0)\
    "