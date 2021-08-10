package com.cloudmaster.aci;
import com.azure.core.credential.TokenCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.core.management.Region;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.management.AzureEnvironment;
import com.azure.resourcemanager.containerinstance.ContainerInstanceManager;
import com.azure.resourcemanager.containerinstance.models.ContainerGroup;

public class App 
{
    public static void main( String[] args )
    {
        try {
            //=============================================================
            // Authenticate
            AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();

            // Azure SDK client builders accept the credential as a parameter
            ContainerInstanceManager manager = ContainerInstanceManager
                .authenticate(credential, profile);

            String containerGroupName = "container-on-vnet";
            String rgName = "container-on-vnet-rg";
            Region region = Region.US_EAST2;
    
            ContainerGroup containerGroup = manager.containerGroups().define(containerGroupName)
                    .withRegion(region)
                    .withNewResourceGroup(rgName)
                    .withLinux()
                    .withPublicImageRegistryOnly()
                    .withoutVolume()
                    .withContainerInstance("nginx", 80)
                    .withNewVirtualNetwork("10.0.0.0/24")
                    .create();

            System.out.println("Container's group IP address: " + containerGroup.ipAddress());

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
