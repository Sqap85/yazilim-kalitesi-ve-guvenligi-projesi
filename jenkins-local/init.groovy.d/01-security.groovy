import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import jenkins.model.Jenkins
import jenkins.install.InstallState

def instance = Jenkins.get()
def realm = new hudson.security.HudsonPrivateSecurityRealm(false)

if (realm.getUser("admin") == null) {
    realm.createAccount("admin", "techstore123")
}

instance.setSecurityRealm(realm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)
instance.setInstallState(InstallState.INITIAL_SETUP_COMPLETED)
instance.save()
