cmake_minimum_required(VERSION 3.20)
project(phantom_pathfinder LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

set(SOURCES
    src/jni_bridge.cpp
    src/world_state.cpp
    src/pathfinder.cpp
    src/path_annotations.cpp
    src/path_directional_scan.cpp
    src/path_signature.cpp
    src/path_simplifier.cpp
    src/etherwarp_raymarch.cpp
    src/etherwarp_search.cpp
    src/teleport_first.cpp
)

add_library(phantom_pathfinder SHARED ${SOURCES})

target_include_directories(phantom_pathfinder PRIVATE
    src
    ${JNI_INCLUDE_DIRS}
)

target_link_libraries(phantom_pathfinder PRIVATE ${JNI_LIBRARIES})

if(MSVC)
    target_compile_options(phantom_pathfinder PRIVATE /O2 /W3)
    set_target_properties(phantom_pathfinder PROPERTIES
        WINDOWS_EXPORT_ALL_SYMBOLS OFF)
endif()
