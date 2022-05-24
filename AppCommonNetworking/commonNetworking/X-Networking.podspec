Pod::Spec.new do |spec|
    spec.name                     = 'X-Networking'
    spec.version                  = '0.0.23'
    spec.homepage                 = 'Link to the Shared Module homepage'
    spec.source                   = { :git => 'https://github.com/soramitsu/x-networking.git', :tag => '0.0.23' }
    spec.authors                  = ''
    spec.license                  = ''
    spec.summary                  = 'Some description for the Shared Module'
    spec.vendored_frameworks      = 'AppCommonNetworking/commonNetworking/build/bin/universal/release/X_Networking.framework'
    spec.libraries                = 'c++'
    spec.ios.deployment_target = '11.0'
                
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':commonNetworking',
        'PRODUCT_MODULE_NAME' => 'X-Networking',
	'ENABLED_TESTABILITY' => 'NO',
	'ONLY_ACTIVE_ARCH' => 'YES'
    }
                
end