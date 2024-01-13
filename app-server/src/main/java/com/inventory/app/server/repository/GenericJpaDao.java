package com.inventory.app.server.repository;

import java.io.Serializable;

public class GenericJpaDao<T extends Serializable > extends AbstractJpaDao < T > implements IGenericDao < T > {
}
