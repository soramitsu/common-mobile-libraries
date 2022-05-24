Pod::Spec.new do |spec|
    spec.name                     = 'X-Networking'
    spec.version                  = '0.0.22'
    spec.homepage                 = 'Link to the Shared Module homepage'
    spec.source                   = { :git => 'https://github.com/soramitsu/x-networking.git', :tag => '0.0.22' }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Some description for the Shared Module'
    spec.vendored_frameworks      = 'AppCommonNetworking/commonNetworking/build/cocoapods/framework/commonNetworking.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '11.0'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':commonNetworking',
        'PRODUCT_MODULE_NAME' => 'X-Networking',
	'ENABLED_TESTABILITY' => 'NO',
	'ONLY_ACTIVE_ARCH' => 'YES'
    }
                
    spec.script_phases = [
        {
            :name => 'Build commonNetworking',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$COCOAPODS_SKIP_KOTLIN_BUILD" ]; then
                  echo "Skipping Gradle build task invocation due to COCOAPODS_SKIP_KOTLIN_BUILD environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                echo "Hello from future"
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$HOME/.cocoapods/repos/commonNetworking/AppCommonNetworking/gradlew" -p "$HOME/.cocoapods/repos/commonNetworking/AppCommonNetworking" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end