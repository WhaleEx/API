package com.whaleex.api.client.util.types;

public class TypePermissionLevel implements EosType.Packer {

    private TypeAccountName actor;

    private TypePermissionName permission;

    public TypePermissionLevel(String accountName, String permissionName) {
        actor = new TypeAccountName(accountName);
        permission = new TypePermissionName(permissionName);
    }

    @Override
    public void pack(EosType.Writer writer) {

        actor.pack(writer);
        permission.pack(writer);
    }
}
