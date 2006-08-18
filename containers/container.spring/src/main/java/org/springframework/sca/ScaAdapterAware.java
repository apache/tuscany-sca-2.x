package org.springframework.sca;

/**

/**
 * Interface that enables beans to find the ScaAdapter they are
 * defined with.
 *
 * Note that in most circumstances there is no need for a bean to
 * implement this interface.
 *
 * @author Andy Piper
 * @since 2.1
 */
public interface ScaAdapterAware
{
  void setScaAdapter(ScaAdapter adapter);
}
