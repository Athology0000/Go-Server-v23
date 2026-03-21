cmake_minimum_required(VERSION 3.20)
project(cobalt_pathfinder LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

find_package(JNI REQUIRED)

set(SOURCES
    src/pathfinder_jni.cpp
    src/engine/PathfinderEngine.cpp
    src/engine/AStarPlanner.cpp
    src/engine/PathExecutor.cpp
    src/engine/MovementExpander.cpp
    src/engine/WorldAccessor.cpp
    src/engine/RotationController.cpp
    src/engine/StuckDetector.cpp
)

add_library(cobalt_pathfinder SHARED ${SOURCES})

target_include_directories(cobalt_pathfinder PRIVATE
    include
    ${JNI_INCLUDE_DIRS}
)

target_link_libraries(cobalt_pathfinder PRIVATE ${JNI_LIBRARIES})

if(MSVC)
    target_compile_options(cobalt_pathfinder PRIVATE /O2 /W3)
    set_target_properties(cobalt_pathfinder PROPERTIES
        WINDOWS_EXPORT_ALL_SYMBOLS OFF)
endif()
